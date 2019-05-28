package api;

import api.common.CommonResource;
import core.InspectionsService;
import pojos.RegularScheduleEntry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides implementation for API calls requesting to decide on conflicts presence
 * or other data-based algorithms
 */
@Path("schedule/inspect")
public class ScheduleInspectionsResource extends CommonResource {
    private final InspectionsService insp = new InspectionsService(db);

    @GET
    public Response validateEntireSchedule() throws SQLException {
        List<InspectionResult> results = new ArrayList<>();

        List<List<RegularScheduleEntry>> tc = insp.runOneTeacherManyClassroomsInspection();
        if (!tc.isEmpty()) {
            tc.forEach(entries -> {
                InspectionResult r = new InspectionResult(ResultType.ONE_TEACHER_MANY_CLASSROOMS, entries);
                results.add(r);
            });
        }

        List<List<RegularScheduleEntry>> cs = insp.runOneClassroomManySubjectsInspection();
        if (!cs.isEmpty()) {
            cs.forEach(entries -> {
                InspectionResult r = new InspectionResult(ResultType.ONE_CLASSROOM_MANY_SUBJECTS, entries);
                results.add(r);
            });
        }

        List<List<RegularScheduleEntry>> ct = insp.runOneClassroomManyTeachersInspection();
        if (!ct.isEmpty()) {
            ct.forEach(entries -> {
                InspectionResult r = new InspectionResult(ResultType.ONE_CLASSROOM_MANY_TEACHERS, entries);
                results.add(r);
            });
        }

        return Response.ok().entity(gson.toJson(results)).build();
    }

    enum ResultType {
        ONE_TEACHER_MANY_CLASSROOMS(1, 1),
        ONE_CLASSROOM_MANY_TEACHERS(1, 2),
        ONE_CLASSROOM_MANY_SUBJECTS(1, 3);

        private int code;
        private int type;

        ResultType(int type, int code) {
            this.type = type;
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public int getType() {
            return type;
        }
    }

    class InspectionResult {
        private Integer resultType; // conflict/tip/warning
        private Integer resultCode; // unique code describing specific response
        private long weekday;
        private long classNumber;
        private List<Conflicted> conflicted;

        public InspectionResult(ScheduleInspectionsResource.ResultType rtype, List<RegularScheduleEntry> entries) {
            this.resultCode = rtype.getCode();
            this.resultType = rtype.getType();
            this.weekday = entries.get(0).getWeekDay();
            this.classNumber = entries.get(0).getClassNumber();
            this.conflicted = new ArrayList<>();
            for (RegularScheduleEntry e: entries) {
                this.conflicted.add(new Conflicted(
                        e.getTeacherFullName(), e.getWeekParity(), e.getClassroomName(),
                        e.getGroupNumber(), e.getSubjectName()
                ));
            }
        }

        public Integer getResultType() {
            return resultType;
        }

        public Integer getResultCode() {
            return resultCode;
        }

        public long getWeekday() {
            return weekday;
        }

        public long getClassNumber() {
            return classNumber;
        }

        public List<Conflicted> getConflicted() {
            return conflicted;
        }

        public class Conflicted {
            private String teacherFullName;
            private String weekParity;
            private String classroom;
            private String groupNumber;
            private String subjectName;

            public Conflicted(String teacherFullName, String weekParity, String classroom, String groupNumber, String subjectName) {
                this.teacherFullName = teacherFullName;
                this.weekParity = weekParity;
                this.classroom = classroom;
                this.groupNumber = groupNumber;
                this.subjectName = subjectName;
            }

            public String getTeacherFullName() {
                return teacherFullName;
            }

            public String getWeekParity() {
                return weekParity;
            }

            public String getClassroom() {
                return classroom;
            }

            public String getGroupNumber() {
                return groupNumber;
            }

            public String getSubjectName() {
                return subjectName;
            }
        }
    }
}
