package api;

import pojos.Classroom;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

@Path("/classrooms")
@PermitAll
public class ClassroomsResource extends CommonResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOfClassrooms() throws SQLException {
        List<Classroom> classrooms = db.getClassrooms();
        return Response.ok(gson.toJson(classrooms)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{classroomId}")
    public Response getClassroom(@PathParam("classroomId") Long classroomId) throws SQLException {
        Classroom classroom = db.getClassroom(classroomId);
        return Response.ok(gson.toJson(classroom)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/free")
    public Response getFreeClassrooms(@QueryParam("date") String date, @QueryParam("classNumber") Long classNumber)
            throws SQLException, ParseException {
        if (date == null || classNumber == null) {
            return RESPONSE_BAD_REQUEST.entity("'date' and 'classNumber' fields should not be empty").build();
        }
        List<Classroom> classrooms = db.getFreeClassroomsForDateAndTime(parseDate(date), classNumber);
        return Response.ok(gson.toJson(classrooms)).build();
    }

}
