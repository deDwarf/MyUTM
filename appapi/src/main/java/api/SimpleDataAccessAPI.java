package api;

import core.Roles;
import exceptions.InvalidDateFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojos.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Provides access to very common data, like lists of teachers, students, schedule, etc.
 */
@Path("/")
@PermitAll
public class SimpleDataAccessAPI extends CommonResource {
    private Logger log = LoggerFactory.getLogger(getClass());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Roles.STUDENT, Roles.TEACHER, Roles.ADMIN})
    public Response hello() {
        return Response.status(200, "Hey there!").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("profile")
    @RolesAllowed({Roles.STUDENT, Roles.TEACHER})
    public Response getMyProfile(@Context SecurityContext sec) throws SQLException {
        if (sec.isUserInRole(Roles.STUDENT)) {
            Student st = db.getStudent(sec.getUserPrincipal().getName());
            if (st == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("User not found").build();
            }
            return Response.ok(gson.toJson(st)).build();
        } else if (sec.isUserInRole(Roles.TEACHER)) {
            // TODO
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unrecognized role").build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("profile")
    @RolesAllowed({Roles.STUDENT, Roles.TEACHER})
    public Response updateMyProfile(@Context SecurityContext sec,
                                    @QueryParam("groupId") Long groupId,
                                    @QueryParam("groupNumber") String groupNumber) throws SQLException {
        Student st = db.getStudent(sec.getUserPrincipal().getName());
        if (sec.isUserInRole(Roles.STUDENT)) {
            if (groupId != null && groupNumber != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("You must specify either groupNumber of groupId, but not both of them!")
                        .build();
            }
            if (groupId != null) {
                Group g = db.getGroupById(groupId);
                if (g == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Specified group id is not valid: " + groupId)
                            .build();
                }
                db.updateStudentParam(st.getStudentId(), "group_id", g.getGroupId());
            }
            if (groupNumber != null) {
                Group g = db.getGroupByName(groupNumber);
                if (g == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Specified group number is not valid: " + groupNumber)
                            .build();
                }
                db.updateStudentParam(st.getStudentId(), "group_id", g.getGroupId());
            }
            return Response.ok().build();
        } else if (sec.isUserInRole(Roles.TEACHER)) {
            // TODO
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unrecognized role").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("myschedule/{from-date}")
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
    @Path("groups/{group}/schedule/{date}")
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
    @Path("groups/{group}/schedule/")
    public Response getGroupSchedule(@PathParam("group") Long groupId) throws SQLException {
        List<RegularScheduleEntry> schedule = db.getRegularSchedule(groupId);

        return Response.ok(gson.toJson(schedule)).build();
    }

    /**
     * Returns schedule for given teacher and day
     *
     * @param teacherId teacher for which schedule is requested. This param should be teacher ID
     * @param date day for which schedule is requested. Only date part is considered
     * @return Same as {@link api.SimpleDataAccessAPI#getStudentScheduleForDay(Long, String, SecurityContext)}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teachers/{teacherId}/schedule/{date}")
    public Response getTeacherScheduleForDay(
            @PathParam("teacherId") Long teacherId,
            @PathParam("date") String date,
            @Context SecurityContext sec) throws SQLException {
        Date parsedDate = parseDate(date);
        List<ScheduleEntry> schedule = db.getTeacherScheduleForDay(parsedDate, teacherId);

        return Response.ok(gson.toJson(schedule)).build();
    }

    private Date parseDate(String date) {
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            parsedDate = f.parse(date);
        } catch (ParseException e) {
            throw new InvalidDateFormatException("date", date);
        }
        return parsedDate;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teachers/{teacherId}/schedule/")
    public Response getTeacherSchedule(@PathParam("teacherId") String teacherId) {
        /*
        repetitive: {
            odd: [scheduleUnit]
            even: [scheduleUnit]
            both: [scheduleUnit]
        }
        dated: {
            leftBorderDate: date
            rightBorderDate: date
            classes: [
                scheduleUnit
            ]
        }
         */
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }



}
