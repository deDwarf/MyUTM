package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.Context;
import core.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojos.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Provides access to very common data, like lists of teachers, students, schedule, etc.
 */
@Path("/")
public class SimpleDataAccessAPI {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd")
            .create();
    private Database db = Context.getInstance().getDB();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response hello() {
        return Response.status(418, "I`m a teapot!").build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teachers")
    public Response getListOfTeachers() throws SQLException {
        List<Teacher> teachers = db.getTeachers();

        return Response.ok(gson.toJson(teachers)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("groups")
    public Response getListOfGroups() throws SQLException {
        List<Group> groups = db.getGroups();

        return Response.ok(gson.toJson(groups)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("classrooms")
    public Response getListOfClassrooms() throws SQLException {
        List<Classroom> classrooms = db.getClassrooms();

        return Response.ok(gson.toJson(classrooms)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("subjects")
    public Response getListOfSubjects() throws SQLException {
        List<Subject> subjects = db.getSubjects();

        return Response.ok(gson.toJson(subjects)).build();
    }

    /**
     * Returns schedule for given group and day
     *
     * @param group ID of a group for which schedule is requested.
     * @param date day for which schedule is requested. Only date part is considered
     * @return List of objects with all necessary schedule information, both "regular" and "dated".
     *         Cancelled regular classes will be marked with "cancelled" flag set to True.
     *         Cancelled dated classes are not persisted and thus won't be returned by this method.
     */
    @SuppressWarnings("Duplicates")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("schedule/groups/{group}/{date}")
    public Response getStudentScheduleForDay(
            @PathParam("group") int group,
            @PathParam("date") String date) throws SQLException {
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            parsedDate = f.parse(date);
        } catch (ParseException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(String.format("Failed to parse 'date' field for value <%s>. " +
                            "Check if date follows 'yyyy-MM-dd' format", date))
                    .build();
        }
        List<ScheduleEntry> schedule = db.getGroupScheduleForDay(parsedDate, group);

        return Response.ok(gson.toJson(schedule)).build();
    }

    /**
     * Returns schedule for given teacher and day
     *
     * @param teacherId teacher for which schedule is requested. This param should be teacher ID
     * @param date day for which schedule is requested. Only date part is considered
     * @return Same as {@link api.SimpleDataAccessAPI#getStudentScheduleForDay(int, String)}
     */
    @SuppressWarnings("Duplicates")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("schedule/teachers/{teacherId}/{date}")
    public Response getTeacherScheduleForDay(
            @PathParam("teacherId") int teacherId,
            @PathParam("date") String date) throws SQLException {
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            parsedDate = f.parse(date);
        } catch (ParseException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(String.format("Failed to parse 'date' field for value <%s>. " +
                            "Check if date follows 'yyyy-MM-dd' format", date))
                    .build();
        }
        List<ScheduleEntry> schedule = db.getTeacherScheduleForDay(parsedDate, teacherId);
        return Response.ok(gson.toJson(schedule)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("schedule/teachers/{group}")
    public Response getGroupSchedule(@PathParam("group") String group) {
        return Response.ok("").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("schedule/groups/{teacherId}")
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
        return Response.ok("").build();
    }



}
