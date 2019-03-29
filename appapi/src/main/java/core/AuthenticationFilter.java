package core;

import api.AuthAPI;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String authHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (!isTokenBasedAuthentication(authHeader)) {
            return;
        }
        Claims jwtClaims;
        try {
            // silently validates token and throws exceptions if invalid
            jwtClaims = Jwts.parser()
                    .setSigningKey(AuthAPI.KEY)
                    .parseClaimsJws(authHeader.substring(7))
                    .getBody();
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            log.debug("Exception occurred while parsing JWT: " + e.getMessage());
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid authorization token").build());
            return;
        }

        SecurityContext secContext = containerRequestContext.getSecurityContext();
        containerRequestContext.setSecurityContext(new JWTSecurityContext(secContext, jwtClaims));
    }


    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }
}

class JWTSecurityContext implements SecurityContext {

    private SecurityContext originalSecurityContext;
    private Claims jwtClaims;

    public JWTSecurityContext(SecurityContext context, Claims claims) {
        this.originalSecurityContext = context;
        this.jwtClaims = claims;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> jwtClaims.get("user", String.class);
    }

    @Override
    public boolean isUserInRole(String s) {
        String role = jwtClaims.get("role", String.class);
        if (role == null) {
            return false;
        }
        return role.equals(s);
    }

    @Override
    public boolean isSecure() {
        return originalSecurityContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return originalSecurityContext.getAuthenticationScheme();
    }

}
