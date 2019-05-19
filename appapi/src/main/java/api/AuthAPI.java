package api;

import api.common.CommonResource;
import authpojos.Account;
import core.AppContext;
import core.Database;
import core.Roles;
import defuse.PasswordStorage;
import defuse.PasswordStorage.CannotPerformOperationException;
import defuse.PasswordStorage.InvalidHashException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import pojos.Teacher;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.security.Key;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides implementation for authorizing users and issuing tokens
 *
 * TODO (vital!) create account, user and link them together inside a transaction
 * TODO introduce refresh-token instead of refreshing using the same access_token
 */
@Path("auth/")
@PermitAll
public class AuthAPI extends CommonResource {
    private Database db = AppContext.getInstance().getDB();
    public static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/teapot")
    public Response stub() {
        return Response.status(418, "I`m a teapot!").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("token/")
    public Response authenticateUser(@QueryParam("grant_type") final String grantType,
                                     @QueryParam("username") String username,
                                     @QueryParam("password") String password,
                                     @QueryParam("refresh_token") String refreshToken)
            throws CannotPerformOperationException, InvalidHashException, SQLException {
        Map<String, String> response = new HashMap<>();
        if ("password".equals(grantType)) {
            if (username == null || password == null) {
                return RESPONSE_BAD_REQUEST.build();
            }
            Account u = db.getAccountByUsername(username);
            if (!validateUserNameAndPassword(u, username, password)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            String token = issueToken(username, u.getUserRole());
            response.put("access_token", token);
            response.put("role", u.getUserRole().toUpperCase());
            response.put("username", u.getUserLogin());
            return Response.ok(AppContext.getInstance().GSON.toJson(response)).build();

        } else if ("refresh_token".equals(grantType)) {
            if (refreshToken == null) {
                return RESPONSE_BAD_REQUEST.build();
            }
            Claims jwtClaims;
            try {
                jwtClaims = Jwts.parser()
                        .setSigningKey(KEY)
                        .parseClaimsJws(refreshToken)
                        .getBody();
                String newToken = issueToken(
                        jwtClaims.get("user", String.class),
                        jwtClaims.get("role", String.class),
                        jwtClaims.get("chain_uuid", String.class)
                );
                response.put("access_token", newToken);
                response.put("role", jwtClaims.get("role", String.class).toUpperCase());
                response.put("username", jwtClaims.get("user", String.class).toUpperCase());
                return Response.ok(gson.toJson(response)).build();
            } catch (Exception e) {
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid token").build();
            }
        }
        else {
            return RESPONSE_BAD_REQUEST.entity("Unsupported grant type: " + grantType).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("tokeninfo/")
    public Response tokeninfo(@QueryParam("access_token") String accessToken) {
        Claims jwtClaims;
        Map<String, String> response = new HashMap<>();
        try {
            jwtClaims = Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(accessToken)
                    .getBody();
            response.put("user", jwtClaims.get("user", String.class));
            response.put("role", jwtClaims.get("role", String.class));
            response.put("exp", this.formatDate(jwtClaims.getExpiration()));
            return Response.ok(gson.toJson(response)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid token").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("register/student/")
    public Response registerStudent(@QueryParam("groupId") Integer groupId,
                                 @QueryParam("username") String username,
                                 @QueryParam("password") String password,
                                 @QueryParam("firstNm") String firstNm,
                                 @QueryParam("secondNm") String secondNm)
            throws CannotPerformOperationException, SQLException {
        if (username == null || password == null || firstNm == null || secondNm == null) {
            return RESPONSE_BAD_REQUEST.entity("Username, password, first and last names must not be null").build();
        }
        Map<String, String> response = new HashMap<>();
        if (db.getAccountByUsername(username) != null || db.getTeacher(username) != null) {
            response.put("success", "false");
            response.put("reason", "Specified email address is already in use");
            response.put("reason_code", "1");
            return Response.ok(AppContext.getInstance().GSON.toJson(response)).build();
        }
        String passwordHash = PasswordStorage.createHash(password);
        BigInteger acctId = db.createUserAccount(username, passwordHash, Roles.STUDENT);
        BigInteger studId = db.createStudent(username, firstNm, secondNm, groupId);
        db.linkStudentWithAccount(studId, acctId);

        String token = issueToken(username, "STUDENT");
        response.put("access_token", token);
        response.put("role", Roles.STUDENT);
        response.put("username", username);
        response.put("success", "true");

        return Response.ok(AppContext.getInstance().GSON.toJson(response)).build();
    }

    @POST
    @RolesAllowed(Roles.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("register/teacher/")
    public Response registerTeacher(@QueryParam("teacherId") BigInteger teacherId,
                                    @QueryParam("password") String password)
            throws CannotPerformOperationException, SQLException {
        if (password == null || teacherId == null) {
            return RESPONSE_BAD_REQUEST.entity("password and teacherId must not be null").build();
        }
        Map<String, String> response = new HashMap<>();
        Teacher t = db.getTeacher(teacherId.longValue());
        if (db.getAccountByUsername(t.getPrimaryEmail()) != null) {
            response.put("success", "false");
            response.put("reason", "Specified teacher already has an account");
            response.put("reason_code", "2");
            return Response.ok(AppContext.getInstance().GSON.toJson(response)).build();
        }
        String passwordHash = PasswordStorage.createHash(password);
        BigInteger acctId = db.createUserAccount(t.getPrimaryEmail(), passwordHash, Roles.TEACHER);
        db.linkTeacherWithAccount(teacherId, acctId);
        response.put("success", "true");

        return Response.ok(AppContext.getInstance().GSON.toJson(response)).build();
    }

    private boolean validateUserNameAndPassword(Account u, String username, String password)
            throws InvalidHashException, CannotPerformOperationException {
        if (u == null) {
            return false;
        }
        if (!validateNotEmpty(username, password, u.getUserLogin())) {
            return false;
        }

        return PasswordStorage.verifyPassword(password, u.getPasswordHash());
    }

    private String issueToken(String username, String userRole) {
        return issueToken(username, userRole, UUID.randomUUID().toString());
    }

    private String issueToken(String username, String userRole, String chainId) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        return Jwts.builder()
                .claim("user", username)
                .claim("role", userRole)
                .claim("chain_uuid", chainId)
                .setExpiration(c.getTime())
                .signWith(KEY)
                .compact();
    }

    private Response constructBadRegistrationResponse(String reason, int reasonCode) {
        Map<String, String> response = new HashMap<>();
        response.put("success", "false");
        response.put("reason", reason);
        response.put("reason_code", String.valueOf(reasonCode));

        return Response.ok(AppContext.getInstance().GSON.toJson(response)).build();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateNotEmpty(String... params) {
        for (String p: params) {
            if (p == null || p.trim().equals("")) {
                return false;
            }
        }
        return true;
    }
}
