package api;

import api.common.SimpleCUDResource;
import pojos.Teacher;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/teachers")
@PermitAll
public class TeachersResource extends SimpleCUDResource<Teacher> {
    public TeachersResource() {
        super(Teacher.class, "teachers");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOfTeachers() throws SQLException {
        List<Teacher> teachers = db.getTeachers();
        return Response.ok(gson.toJson(teachers)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{teacherId}")
    public Response getTeacher(@PathParam("teacherId") Long teacherId) throws SQLException {
        Teacher teacher = db.getTeacher(teacherId);
        return Response.ok(gson.toJson(teacher)).build();
    }

    @Override
    @POST
    public Response add(String json) {
        return super.add(json);
    }


    @Override
    @PUT
    public Response update(String params) {
        return super.update(params);
    }

    @Override
    @DELETE
    @Path("/{teacherId}")
    public Response delete(@PathParam("teacherId") Long id) {
        return super.delete(id);
    }
}
