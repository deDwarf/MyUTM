package api;

import api.common.CommonResource;
import api.common.Message;
import com.google.gson.reflect.TypeToken;
import core.AppContext;
import core.Roles;
import core.exceptions.BadRequestRuntimeException;
import exporter.ScheduleExporter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojos.*;
import pojos.helper.EntryIdAndGroupIdAndGroupName;
import pojos.req.CancelLessonRequestEntity;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
@Path("/schedule")
public class ScheduleResource extends CommonResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Type intListType = new TypeToken<List<Long>>(){}.getType();

    @POST
    @RolesAllowed({Roles.ADMIN})
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/regular")
    public Response registerRegularClass(String data) throws SQLException {
        RegularScheduleEntry parsedData = gson.fromJson(data, RegularScheduleEntry.class);
        RegularScheduleEntry entry = db.registerRegularLesson("fcimapp.schedule", parsedData);
        fcm.notifyLessonRegistered(entry);
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
        // date(s), ids
        // {date: "", ids: [{type: "", id: ""}, {}..]
        CancelLessonRequestEntity input = gson.fromJson(data, CancelLessonRequestEntity.class);
        db.cancelDatedClasses(input.getIds().stream()
                .filter(e -> "dated".equals(e.getScheduleEntryType()))
                .map(EntryIdAndGroupIdAndGroupName::getScheduleEntryId)
                .collect(Collectors.toList()));
        db.cancelRegularClasses(input.getDate(), input.getIds().stream()
                .filter(e -> "regular".equals(e.getScheduleEntryType()))
                .map(EntryIdAndGroupIdAndGroupName::getScheduleEntryId)
                .collect(Collectors.toList()));
        return Response.ok().build();
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
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Roles.ADMIN, Roles.TEACHER})
    @Path("/dated")
    public Response registerDatedClass(String json) throws SQLException {
        // groups, teacher, date, class_number, classroom_id, subject_id, subject_type_id
        ScheduleEntry scheduleEntry;
        try {
            scheduleEntry = gson.fromJson(json, ScheduleEntry.class);
        } catch (Exception e) {
            return Response.status(400).entity("Invalid request: <" + json + ">").build();
        }
        if (scheduleEntry.getTeacherId() == 0 && scheduleEntry.getTeacherUsername() != null) {
            Teacher t = db.getTeacher(scheduleEntry.getTeacherUsername());
            scheduleEntry.setTeacherId(Math.toIntExact(t.getTeacherId()));
        }
        Long id = db.registerDatedClass(scheduleEntry);
        fcm.notifyLessonRegistered(db.getDatedScheduleEntry(id));
        return Response.ok(gson.toJson(new Message(String.valueOf(id)))).build();
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
        return getStudentScheduleForDay(groupId, date, date, sec);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("groups/{group}/{from-date}/{till-date}")
    public Response getStudentScheduleForDay(
            @PathParam("group") Long groupId,
            @PathParam("from-date") String fromDate,
            @PathParam("till-date") String tillDate,
            @Context SecurityContext sec) throws SQLException {
        Date parsedFromDate = parseDate(fromDate);
        Date parsedTillDate = parseDate(tillDate);
        List<ScheduleEntry> schedule = db.getGroupScheduleForDay(parsedFromDate, parsedTillDate, groupId);

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
        return getTeacherScheduleForDay(teacherId, date, date, sec);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teachers/{teacherId}/{from-date}/{till-date}")
    public Response getTeacherScheduleForDay(
            @PathParam("teacherId") Long teacherId,
            @PathParam("from-date") String fromDate,
            @PathParam("till-date") String tillDate,
            @Context SecurityContext sec) throws SQLException {
        Date parsedFromDate = parseDate(fromDate);
        Date parsedTillDate = parseDate(tillDate);
        List<GroupedScheduleEntry> schedule = db.getTeacherGroupedScheduleForDay(parsedFromDate, parsedTillDate, teacherId);

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
        return getMySchedule(sec, fromDate, fromDate);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("my/{from-date}/{till-date}")
    @RolesAllowed({Roles.STUDENT, Roles.TEACHER})
    public Response getMySchedule(@Context SecurityContext sec,
                                  @PathParam("from-date") String fromDate,
                                  @PathParam("till-date") String tillDate) throws SQLException {
        if (sec.isUserInRole(Roles.STUDENT)) {
            Student st = db.getStudent(sec.getUserPrincipal().getName());
            return getStudentScheduleForDay(st.getGroupId(), fromDate, tillDate, sec);
        } else if (sec.isUserInRole(Roles.TEACHER)) {
            Teacher t = db.getTeacher(sec.getUserPrincipal().getName());
            return getTeacherScheduleForDay(t.getTeacherId(), fromDate, tillDate, sec);
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unrecognized role").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("export/")
    public Response getScheduleExportGroupedByGroups(@FormParam("name") List<String> names,
                                                     @FormParam("type") List<String> types,
                                                     @FormParam("ids") List<String> ids) throws SQLException, IOException {
        List<ExportRequestEntryEntity> input = ExportRequestEntryEntity.create(names, types, ids);
        java.nio.file.Path finalFile = this.exportSchedule(input);
        StreamingOutput so = outputStream -> {
            outputStream.write(Files.readAllBytes(finalFile));
            outputStream.flush();
        };
        log.info("Sending final file.. " + finalFile.toString());
        return Response
                .ok(so, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + finalFile.getFileName().toString() + "\"")
                .build();
    }

    private java.nio.file.Path exportSchedule(List<ExportRequestEntryEntity> entities) throws IOException, SQLException {
        ScheduleExporter e = new ScheduleExporter();
        List<Pair<File, String>> outFiles = new LinkedList<>();
        // generate files
        for (ExportRequestEntryEntity entity: entities) {
            File export;
            if (entity.getType() == ExportRequestEntryEntity.Type.GROUP) {
                export = e.exportStudentSchedule(entity.getIds());
            } else {
                export = e.exportTeacherSchedule(entity.getIds());
            }
            outFiles.add(new ImmutablePair<>(export, entity.getFileName()));
            delayedDeleteFile(export, 2000 * 60);
        }

        java.nio.file.Path finalFile = this.compressAsZip(outFiles);
        delayedDeleteFile(new File(finalFile.toUri()), 2000 * 60);
        return finalFile;
    }

    private java.nio.file.Path compressAsZip(List<Pair<File, String>> files) throws IOException {
        File zip = File.createTempFile("schedule-export-", ".zip", AppContext.getInstance().TMP_DIR);
        ZipOutputStream zou = new ZipOutputStream(new FileOutputStream(zip));
        for (Pair<File, String> file : files) {
            String fileName = file.getRight() == null ? file.getLeft().getName() : file.getRight();
            zou.putNextEntry(new ZipEntry(fileName));
            zou.write(Files.readAllBytes(file.getLeft().toPath()));
            zou.closeEntry();
        }
        zou.flush();
        zou.finish();
        zou.close();

        return zip.toPath();
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
}

class ExportRequestEntryEntity {
    enum Type {TEACHER, GROUP}
    
    private String fileName;
    private Type type;
    private List<Long> ids;

    static List<ExportRequestEntryEntity> create(List<String> names, List<String> types, List<String> ids) {
        List<ExportRequestEntryEntity> entries = new ArrayList<>(names.size());
        normalizeNames(names);
        if (names.size() != types.size() || names.size() != ids.size()) {
            throw new RuntimeException("Invalid input params");
        }
        for (int i = 0; i < names.size(); i++) {
            if (!"".equals(ids.get(i).trim())) {
                List<Long> entryIds = Arrays.stream(ids.get(i).split(",")).map(Long::parseLong).collect(Collectors.toList());
                entries.add(new ExportRequestEntryEntity(names.get(i), types.get(i), entryIds));
            }
        }
        return entries;
    }

    private static void normalizeNames(List<String> names) {
        // make unique
        int counter = 1;
        for (int i = 0; i < names.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (names.get(i).equalsIgnoreCase(names.get(j))) {
                    names.set(i, names.get(i) + String.valueOf(counter++));
                }
            }
        }
        // add extensions
        for (int i = 0; i < names.size(); i++) {
            if (!names.get(i).endsWith(".xls")) {
                names.set(i, names.get(i) + ".xls");
            }
        }
    }
    
    private ExportRequestEntryEntity(String fileName, String type, List<Long> ids) {
        this.fileName = fileName;
        this.ids = ids;
        try {
            this.type = Type.valueOf(type.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestRuntimeException("Unknown export entry type: " + type);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
