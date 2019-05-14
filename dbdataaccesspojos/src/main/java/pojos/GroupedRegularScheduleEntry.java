package pojos;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupedRegularScheduleEntry {
    private static transient final Gson gson = new GsonBuilder().create();

    private Map<Long, Groups> groupIdEntryIdAndGroupNumber;
    private String classroomName;
    private Long subgroup;
    private String teacherFirstName;
    private String teacherSecondName;
    private String teacherMiddleName;
    private String teacherFullName;
    private String subjectTypeAbbreviated;
    private String subjectName;
    private String subjectNameAbbreviated;
    private long weekDay;
    private String weekParity;
    private long classNumber;
    private String classStartTime;
    private String classEndTime;
    private String classType;
    private long subjectId;
    private long teacherId;
    private long classroomId;
    private long subjectTypeId;

    public long getSubjectTypeId() {
        return subjectTypeId;
    }

    public void setSubjectTypeId(long subjectTypeId) {
        this.subjectTypeId = subjectTypeId;
    }

    public long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(long classroomId) {
        this.classroomId = classroomId;
    }


    public Map<Long, Groups> getGroups() {
        return groupIdEntryIdAndGroupNumber;
    }

    public void setGroupIdToEntryIdAndGroupNumber(String groupIdToEntryIdAndGroupNumber) {
        this.groupIdEntryIdAndGroupNumber = new HashMap<>();
        List<Groups> tmp = gson.fromJson(groupIdToEntryIdAndGroupNumber, new TypeToken<List<Groups>>(){}.getType());
        tmp.forEach(e -> this.groupIdEntryIdAndGroupNumber.put(e.getGroupId(), e));
    }


    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }


    public Long getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(Long subgroup) {
        this.subgroup = subgroup;
    }


    public String getTeacherFirstName() {
        return teacherFirstName;
    }

    public void setTeacherFirstName(String teacherFirstName) {
        this.teacherFirstName = teacherFirstName;
    }


    public String getTeacherSecondName() {
        return teacherSecondName;
    }

    public void setTeacherSecondName(String teacherSecondName) {
        this.teacherSecondName = teacherSecondName;
    }


    public String getTeacherMiddleName() {
        return teacherMiddleName;
    }

    public void setTeacherMiddleName(String teacherMiddleName) {
        this.teacherMiddleName = teacherMiddleName;
    }


    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
    }


    public String getSubjectTypeAbbreviated() {
        return subjectTypeAbbreviated;
    }

    public void setSubjectTypeAbbreviated(String subjectTypeAbbreviated) {
        this.subjectTypeAbbreviated = subjectTypeAbbreviated;
    }


    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


    public String getSubjectNameAbbreviated() {
        return subjectNameAbbreviated;
    }

    public void setSubjectNameAbbreviated(String subjectNameAbbreviated) {
        this.subjectNameAbbreviated = subjectNameAbbreviated;
    }


    public long getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(long weekDay) {
        this.weekDay = weekDay;
    }


    public String getWeekParity() {
        return weekParity;
    }

    public void setWeekParity(String weekParity) {
        this.weekParity = weekParity;
    }


    public long getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(long classNumber) {
        this.classNumber = classNumber;
    }


    public String getClassStartTime() {
        return classStartTime;
    }

    public void setClassStartTime(String classStartTime) {
        this.classStartTime = classStartTime;
    }


    public String getClassEndTime() {
        return classEndTime;
    }

    public void setClassEndTime(String classEndTime) {
        this.classEndTime = classEndTime;
    }


    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }


    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }


    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }


    public class Groups {
        private long scheduleEntryId;
        private long groupId;
        private String groupNumber;

        public long getScheduleEntryId() {
            return scheduleEntryId;
        }

        public void setScheduleEntryId(long scheduleEntryId) {
            this.scheduleEntryId = scheduleEntryId;
        }

        public long getGroupId() {
            return groupId;
        }

        public void setGroupId(long groupId) {
            this.groupId = groupId;
        }

        public String getGroupNumber() {
            return groupNumber;
        }

        public void setGroupNumber(String groupNumber) {
            this.groupNumber = groupNumber;
        }
    }
}
