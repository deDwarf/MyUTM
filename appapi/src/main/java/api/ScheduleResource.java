package api;

import com.google.gson.reflect.TypeToken;
import core.Roles;
import pojos.RegularScheduleEntry;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
@Path("/schedule")
public class ScheduleResource extends CommonResource {

    @POST
    @RolesAllowed({Roles.ADMIN})
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/regular")
    public Response registerRegularClass(String data) throws SQLException {
        RegularScheduleEntry parsedData = gson.fromJson(data, RegularScheduleEntry.class);
        RegularScheduleEntry entry = db.registerRegularLesson("fcimapp.schedule", parsedData);
        // group, teacher, date, time, classroom, subject, subject_type
        return Response.ok(gson.toJson(entry)).build();
    }

    @DELETE
    @RolesAllowed({Roles.ADMIN})
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/regular")
    public Response removeRegularClass(String data) throws SQLException {
        List<Integer> ids = gson.fromJson(data, new TypeToken<List<Integer>>(){}.getType());
        int affected = db.removeRegularClasses(ids);
        return Response.ok(affected).build();
    }

    @POST
    @RolesAllowed({Roles.ADMIN, Roles.TEACHER})
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("regular/cancel")
    public Response cancelRegularClass(String data) throws SQLException {
        return RESPONSE_NOT_IMPLEMENTED;
    }

    @POST
    @RolesAllowed({Roles.ADMIN, Roles.TEACHER})
    @Path("regular/postpone")
    public Response postponeRegularClass() {
        // List - to make it possible to postpone the same class for multiple groups
        // list of IDs, newDate, newTime, newClassroom
        return RESPONSE_NOT_IMPLEMENTED;
    }

    @POST
    @RolesAllowed({Roles.ADMIN, Roles.TEACHER})
    @Path("/dated")
    public Response registerDatedClass() {
        // groups, teacher, date, time, classroom, subject, subject_type
        return RESPONSE_NOT_IMPLEMENTED;
    }
}
