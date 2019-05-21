package api;

import com.google.gson.reflect.TypeToken;
import core.Roles;
import exceptions.InvalidDateFormatException;
import exporter.ScheduleExporter;
import pojos.RegularScheduleEntry;
import pojos.ScheduleEntry;
import pojos.Student;
import pojos.Teacher;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
@Path("/schedule")
public class ScheduleResource extends CommonResource {

    private static final Type intListType = new TypeToken<List<Long>>(){}.getType();

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
        List<Integer> ids = gson.fromJson(data, intListType);
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
     *         Cancelled dated classes are not persisted and thus won'timer be returned by this method.
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
        List<ScheduleEntry> schedule = db.getTeacherScheduleForDay(parsedDate, teacherId);

        return Response.ok(gson.toJson(schedule)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teachers/{teacherId}/")
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

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("export/")
    public Response getScheduleExportGroupedByGroups(String input) throws SQLException, IOException {
        ExportRequestPojo req = gson.fromJson(input, ExportRequestPojo.class);
        if (req == null) {
            req = new ExportRequestPojo(Arrays.asList(Collections.singletonList((long)1)), null);
        }
        ScheduleExporter e = new ScheduleExporter();
        try {
            java.nio.file.Path finalFile;
            List<File> outFiles = new LinkedList<>();
            // generate files
            if (req.getStudentIds() != null) {
                for (List<Long> ids : req.getStudentIds()) {
                    File export = e.exportStudentSchedule(ids);
                    outFiles.add(export);
                    delayedDeleteFile(export, 2000 * 60);
                }
            }
            if (req.getTeacherIds() != null) {
                for (List<Long> ids : req.getTeacherIds()) {
                    File export = e.exportTeacherSchedule(ids);
                    outFiles.add(export);
                    delayedDeleteFile(export, 2000 * 60);
                }
            }
            if (outFiles.size() == 1) {
                finalFile = outFiles.get(0).toPath();
            } else {
                finalFile = this.compressAsZip(outFiles);
            }

            // prepare response
            StreamingOutput so = outputStream -> {
                outputStream.write(Files.readAllBytes(finalFile));
                outputStream.flush();
            };
            delayedDeleteFile(new File(finalFile.toUri()), 2000 * 60);
            return Response
                    .ok(so, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = " + finalFile.getFileName().toString())
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Response.status(500).build();
    }

    private Timer timer = new Timer();
    private void delayedDeleteFile(File file, int delay) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean deleted = file.delete();
                if (!deleted) file.deleteOnExit();
            }
        }, delay);
    }

    private java.nio.file.Path compressAsZip(List<File> files) throws IOException {
        java.nio.file.Path zip = Files.createTempFile("schedule-export-", ".zip");
        ZipOutputStream zou = new ZipOutputStream(new FileOutputStream(new File(zip.toUri())));
        for (File file : files) {
            zou.putNextEntry(new ZipEntry(file.getName()));
            zou.write(Files.readAllBytes(file.toPath()));
            zou.closeEntry();
        }
        zou.flush();
        zou.finish();
        zou.close();

        return zip;
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

    private class ExportRequestPojo {
        private List<List<Long>> teacherIds;
        private List<List<Long>> studentIds;

        public ExportRequestPojo(List<List<Long>> teacherIds, List<List<Long>> studentIds) {
            this.teacherIds = teacherIds;
            this.studentIds = studentIds;
        }

        public List<List<Long>> getTeacherIds() {
            return teacherIds;
        }

        public void setTeacherIds(List<List<Long>> teacherIds) {
            this.teacherIds = teacherIds;
        }

        public List<List<Long>> getStudentIds() {
            return studentIds;
        }

        public void setStudentIds(List<List<Long>> studentIds) {
            this.studentIds = studentIds;
        }
    }
}
