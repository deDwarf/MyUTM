package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.Context;
import core.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojos.Classroom;
import pojos.Group;
import pojos.Subject;
import pojos.Teacher;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;


/**
 * Provides access to very common data, like lists of teachers, students, schedule, etc.
 */
@Path("/")
public class SimpleDataAccessAPI {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("dd-MM-yyyy")
            .create();
    private Database db = Context.getInstance().getDB();

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("schedule/groups/{group}/{date}")
    public Response getStudentScheduleForDay(
            @PathParam("group") String group,
            @PathParam("date") String date) {

        return Response.ok("").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("schedule/teachers/{teacherId}/{date}")
    public Response getTeacherScheduleForDay(
            @PathParam("teacherId") String teacherId,
            @PathParam("date") String date) {

        return Response.ok("").build();
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
