package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GenericExceptionHandler implements ExceptionMapper<Exception> {
    private final static Logger log = LoggerFactory.getLogger(GenericExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        log.error(exception.toString());
        return Response.status(500)
                .entity("Internal server error. Please try again in a few minutes")
                .type("text/plain").build();
    }
}
