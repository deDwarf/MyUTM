package api;

import api.common.CommonResource;
import pojos.Teacher;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/teachers")
@PermitAll
public class TeachersResource extends CommonResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOfTeachers() throws SQLException {
        List<Teacher> teachers = db.getTeachers();
        return Response.ok(gson.toJson(teachers)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{teacherId}")
    public Response getTeacher(@PathParam("teacherId") Long teacherId) throws SQLException {
        Teacher teacher = db.getTeacher(teacherId);
        return Response.ok(gson.toJson(teacher)).build();
    }

}
