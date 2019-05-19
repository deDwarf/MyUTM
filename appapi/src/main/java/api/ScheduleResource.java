package api;

import api.common.CommonResource;
import com.google.gson.reflect.TypeToken;
import core.Roles;
import pojos.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.Date;
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

    /**
     * Returns schedule for given group and day
     *
     * @param groupId ID of a group for which schedule is requested.
     * @param date day for which schedule is requested. Only date part is considered
     * @return List of objects with all necessary schedule information, both "regular" and "dated".
     *         Cancelled regular classes will be marked with "cancelled" flag set to True.
     *         Cancelled dated classes are not persisted and thus won't be returned by this method.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("groups/{group}/{date}")
    public Response getStudentScheduleForDay(
            @PathParam("group") Long groupId,
            @PathParam("date") String date,
            @Context SecurityContext sec) throws SQLException {
        Date parsedDate = parseDate(date);
        List<ScheduleEntry> schedule = db.getGroupScheduleForDay(parsedDate, groupId);

        return Response.ok(gson.toJson(schedule)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("groups/{group}/")
    public Response getGroupSchedule(@PathParam("group") Long groupId) throws SQLException {
        List<RegularScheduleEntry> schedule = db.getRegularSchedule(groupId);

        return Response.ok(gson.toJson(schedule)).build();
    }

    /**
     * Returns schedule for given teacher and day
     *
     * @param teacherId teacher for which schedule is requested. This param should be teacher ID
     * @param date day for which schedule is requested. Only date part is considered
     * @return Same as {@link api.ScheduleResource#getStudentScheduleForDay(Long, String, SecurityContext)}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teachers/{teacherId}/{date}")
    public Response getTeacherScheduleForDay(
            @PathParam("teacherId") Long teacherId,
            @PathParam("date") String date,
            @Context SecurityContext sec) throws SQLException {
        Date parsedDate = parseDate(date);
        List<GroupedScheduleEntry> schedule = db.getTeacherGroupedScheduleForDay(parsedDate, teacherId);

        return Response.ok(gson.toJson(schedule)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teachers/{teacherId}/")
    public Response getTeacherSchedule(@PathParam("teacherId") String teacherId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("my/{from-date}")
    @RolesAllowed({Roles.STUDENT, Roles.TEACHER})
    public Response getMySchedule(@Context SecurityContext sec,
                                  @PathParam("from-date") String fromDate) throws SQLException {
        if (sec.isUserInRole(Roles.STUDENT)) {
            Student st = db.getStudent(sec.getUserPrincipal().getName());
            return getStudentScheduleForDay(st.getGroupId(), fromDate, sec);
        } else if (sec.isUserInRole(Roles.TEACHER)) {
            Teacher t = db.getTeacher(sec.getUserPrincipal().getName());
            return getTeacherScheduleForDay(t.getTeacherId(), fromDate, sec);
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unrecognized role").build();
        }
    }
}
