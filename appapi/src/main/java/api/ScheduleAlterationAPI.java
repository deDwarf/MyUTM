package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.AppContext;
import core.Database;
import core.Roles;
import pojos.RegularScheduleEntry;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
// distinguish by some "action" field? it will not work the way it is specified currently
@Path("/schedule")
public class ScheduleAlterationAPI {
    private Database db = AppContext.getInstance().getDB();
    private Gson gson = AppContext.getInstance().GSON;

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

    @POST
    @RolesAllowed({Roles.ADMIN, Roles.TEACHER})
    @Path("/dated")
    public Response registerDatedClass() {
        // groups, teacher, date, time, classroom, subject, subject_type
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @RolesAllowed({Roles.ADMIN})
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("regular/remove")
    public Response removeClass(String data) throws SQLException {
        List<Integer> ids = gson.fromJson(data, new TypeToken<List<Integer>>(){}.getType());
        int affected = db.removeRegularClasses(ids);
        return Response.ok(affected).build();
    }

    @POST
    @RolesAllowed({Roles.ADMIN, Roles.TEACHER})
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("regular/cancel")
    public Response cancelClass(String data) throws SQLException {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @RolesAllowed({Roles.ADMIN, Roles.TEACHER})
    @Path("regular/postpone")
    public Response postponeClass() {
        // List - to make it possible to postpone the same class for multiple groups
        // list of IDs, newDate, newTime, newClassroom
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
