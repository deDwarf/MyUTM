package api;

import api.common.SimpleCUDResource;
import core.Roles;
import pojos.Subject;
import pojos.SubjectType;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/subjects")
@PermitAll
public class SubjectsResource extends SimpleCUDResource<Subject> {

    public SubjectsResource() {
        super(Subject.class, "subjects");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOfSubjects() throws SQLException {
        List<Subject> subjects = db.getSubjects();
        return Response.ok(gson.toJson(subjects)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{subjectId}")
    public Response getSubject(@PathParam("subjectId") Long subjectId) throws SQLException {
        Subject subject = db.getSubject(subjectId);
        return Response.ok(gson.toJson(subject)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("types/")
    public Response getListOfSubjectTypes() throws SQLException {
        List<SubjectType> subjects = db.getSubjectTypes();
        return Response.ok(gson.toJson(subjects)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("types/{subjectTypeId}")
    public Response getSubjectType(@PathParam("subjectTypeId") Long subjectTypeId) throws SQLException {
        SubjectType subjectType = db.getSubjectType(subjectTypeId);
        return Response.ok(gson.toJson(subjectType)).build();
    }

    @Override
    @POST
    @RolesAllowed({Roles.ADMIN})
    public Response add(String json) {
        return super.add(json);
    }

    @Override
    @PUT
    @RolesAllowed({Roles.ADMIN})
    public Response update(@FormParam("pk") Long id,
                           @FormParam("name") String fieldName,
                           @FormParam("value") String fieldValue) {
        return super.update(id, fieldName, fieldValue);
    }

    @Override
    @DELETE
    @RolesAllowed({Roles.ADMIN})
    @Path("/{classroomId}")
    public Response delete(@PathParam("classroomId") Long id) {
        return super.delete(id);
    }

}
