package pojos;

import java.util.Date;

public class ScheduleEntry {
    private long scheduleEntryId;
    private String classType;
    private int cancelledFlg;
    private Date date;
    private String classStartTime;
    private String classEndTime;
    private String classNumber;
    private String classroomName;
    private String groupNumber;
    private Integer subgroup;
    private String teacherFirstName;
    private String teacherMiddleName;
    private String teacherSecondName;
    private String teacherFullName;
    private String subjectTypeAbbreviated;
    private String subjectName;
    private String subjectNameAbbreviated;
    private int teacherId;
    private int subjectId;
    private int groupId;
    private int classroomId;
    private int subjectTypeId;
    private String teacherUsername;

    public String getTeacherUsername() {
        return teacherUsername;
    }

    public void setTeacherUsername(String teacherUsername) {
        this.teacherUsername = teacherUsername;
    }

    public int getSubjectTypeId() {
        return subjectTypeId;
    }

    public void setSubjectTypeId(int subjectTypeId) {
        this.subjectTypeId = subjectTypeId;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public long getScheduleEntryId() {
        return scheduleEntryId;
    }

    public void setScheduleEntryId(long scheduleEntryId) {
        this.scheduleEntryId = scheduleEntryId;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public int getCancelledFlg() {
        return cancelledFlg;
    }

    public void setCancelledFlg(int cancelledFlg) {
        this.cancelledFlg = cancelledFlg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public Integer getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(Integer subgroup) {
        this.subgroup = subgroup;
    }

    public String getTeacherFirstName() {
        return teacherFirstName;
    }

    public void setTeacherFirstName(String teacherFirstName) {
        this.teacherFirstName = teacherFirstName;
    }

    public String getTeacherMiddleName() {
        return teacherMiddleName;
    }

    public void setTeacherMiddleName(String teacherMiddleName) {
        this.teacherMiddleName = teacherMiddleName;
    }

    public String getTeacherSecondName() {
        return teacherSecondName;
    }

    public void setTeacherSecondName(String teacherSecondName) {
        this.teacherSecondName = teacherSecondName;
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

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "scheduleEntryId=" + scheduleEntryId +
                ", classType='" + classType + '\'' +
                ", cancelledFlg=" + cancelledFlg +
                ", date=" + date +
                ", classStartTime='" + classStartTime + '\'' +
                ", classEndTime='" + classEndTime + '\'' +
                ", classroomName='" + classroomName + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", subgroup=" + subgroup +
                ", teacherFirstName='" + teacherFirstName + '\'' +
                ", teacherMiddleName='" + teacherMiddleName + '\'' +
                ", teacherSecondName='" + teacherSecondName + '\'' +
                ", teacherFullName='" + teacherFullName + '\'' +
                ", subjectTypeAbbreviated='" + subjectTypeAbbreviated + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", subjectNameAbbreviated='" + subjectNameAbbreviated + '\'' +
                ", teacherId=" + teacherId +
                ", subjectId=" + subjectId +
                ", groupId=" + groupId +
                '}';
    }
}

