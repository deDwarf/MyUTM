package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.sql.SQLException;

public class SQLExceptionHandler implements ExceptionMapper<SQLException> {
    private final static Logger log = LoggerFactory.getLogger(SQLExceptionHandler.class);

    @Override
    public Response toResponse(SQLException exception) {
        log.error(exception.toString());
        return Response.status(500)
                .entity("Internal server error. Please try again in a few minutes")
                .type("text/plain").build();
    }
}
