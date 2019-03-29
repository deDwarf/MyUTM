package core;

import exceptions.InvalidDateFormatException;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BadRequestExceptionHandler implements ExceptionMapper<InvalidDateFormatException> {

    @Override
    public Response toResponse(InvalidDateFormatException e) {
        LoggerFactory.getLogger(this.getClass()).info(e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
}
