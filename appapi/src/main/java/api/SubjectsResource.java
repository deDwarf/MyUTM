package api;

import pojos.Subject;
import pojos.SubjectType;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/subjects")
@PermitAll
public class SubjectsResource extends CommonResource {

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

}
