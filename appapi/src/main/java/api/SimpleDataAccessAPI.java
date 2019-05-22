package api;

import api.common.CommonResource;
import core.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojos.ClassesTimeSchedule;
import pojos.Group;
import pojos.Student;
import pojos.Teacher;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
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
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("User not found").build();
            }
            return Response.ok(gson.toJson(st)).build();
        } else if (sec.isUserInRole(Roles.TEACHER)) {
            Teacher t = db.getTeacher(sec.getUserPrincipal().getName());
            if (t == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("User not found").build();
            }
            return Response.ok(gson.toJson(t)).build();
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
    @Path("lesson-numbers/free")
    public Response getAvailableLessonNumbers(@QueryParam("teacherUsername") String teacherUsername,
                                              @QueryParam("groupId") Long groupId,
                                              @QueryParam("groupNumber") String groupNumber,
                                              @QueryParam("date") String date) throws SQLException {
        if (!validateNotEmpty(teacherUsername, date) || groupId == null && groupNumber == null) {
            return Response.status(400)
                    .entity("{msg: \"Teacher username, date and either group number or id must not be empty\"}")
                    .build();
        }
        Teacher t = db.getTeacher(teacherUsername);
        if (groupId == null) {
            Group g = db.getGroupByName(groupNumber);
            groupId = g.getGroupId();
        }
        List<ClassesTimeSchedule> ct = db.getFreeTimeForDateAndTeacherAndGroup(parseDate(date), groupId, t.getTeacherId());

        return Response.ok(gson.toJson(ct)).build();
    }
}
