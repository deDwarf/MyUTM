package core;

import core.exceptions.BadRequestRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.sql.SQLException;

public class BadRequestRuntimeExceptionHandler implements ExceptionMapper<BadRequestRuntimeException> {
    private final static Logger log = LoggerFactory.getLogger(BadRequestRuntimeExceptionHandler.class);

    @Override
    public Response toResponse(BadRequestRuntimeException exception) {
        log.error(exception.toString());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
