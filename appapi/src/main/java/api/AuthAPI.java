package api;

import authpojos.Account;
import core.Context;
import core.Database;
import defuse.PasswordStorage;
import defuse.PasswordStorage.CannotPerformOperationException;
import defuse.PasswordStorage.InvalidHashException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Provides implementation for authorizing users and issuing tokens
 */
@Path("auth/")
public class AuthAPI {
    private Database db = Context.getInstance().getDB();
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("login/")
    public Response authenticateUser(@FormParam("username") String username,
                                     @FormParam("password") String password)
            throws CannotPerformOperationException, InvalidHashException, SQLException {
        Account u = getUserByUsername(username);
        if (! validateUserNameAndPassword(u, username, password)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        String token = issueToken(username, u.getRoleName());

        return Response.ok(token).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("register/")
    public Response registerUser(@FormParam("groupId") int groupId,
                                 @FormParam("username") String username,
                                 @FormParam("password") String password,
                                 @FormParam("firstNm") String firstNm,
                                 @FormParam("secondNm") String secondNm)
            throws CannotPerformOperationException, SQLException {
        Form form = new Form();
        if (getUserByUsername(username) != null) {
            form.param("success", "false");
            return Response.ok(Entity.form(form)).build();
        }
        String passwordHash = PasswordStorage.createHash(password);
        int acctId = db.createUserAccount(username, passwordHash, "STUDENT");
        int studId = db.createStudent(username, firstNm, secondNm, groupId);
        db.linkStudentWithAccount(studId, acctId);
        form.param("success", "true");

        return Response.ok(Entity.form(form)).build();
    }

    private Account getUserByUsername(String username) throws SQLException {
        return db.getAccountByUsername(username);
    }

    private boolean validateUserNameAndPassword(Account u, String username, String password)
            throws InvalidHashException, CannotPerformOperationException {
        if (username == null || password == null || u.getUserLogin() == null) {
            return false;
        }

        return PasswordStorage.verifyPassword(password, u.getPasswordHash());
    }

    private String issueToken(String username, String userRole) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 12);
        return Jwts.builder()
                .claim("role", userRole)
                .setSubject(username)
                .setExpiration(c.getTime())
                .signWith(key)
                .compact();
    }

}
