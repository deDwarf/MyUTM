package pojos;

import java.util.Date;

public class ScheduleEntry {
    private long scheduleEntryId;
    private String classType;
    private int cancelledFlg;
    private Date date;
    private String classTimeInterval;
    private String classroomName;
    private String groupNumber;
    private Integer subgroup;
    private String teacherFullName;
    private String subjectType;
    // private String subjectTypeAbbreviated;
    private String subjectName;
    private String subjectNameAbbreviated;
    private int teacherId;
    private int subjectId;
    private int groupId;

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

    public String getClassTimeInterval() {
        return classTimeInterval;
    }

    public void setClassTimeInterval(String classTimeInterval) {
        this.classTimeInterval = classTimeInterval;
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

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

//    public String getSubjectTypeAbbreviated() {
//        return subjectTypeAbbreviated;
//    }
//
//    public void setSubjectTypeAbbreviated(String subjectTypeAbbreviated) {
//        this.subjectTypeAbbreviated = subjectTypeAbbreviated;
//    }

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

    public void setTeacherId(int mainTeacherId) {
        this.teacherId = mainTeacherId;
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

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "scheduleEntryId=" + scheduleEntryId +
                ", classType='" + classType + '\'' +
                ", cancelledFlg=" + cancelledFlg +
                ", date=" + date +
                ", classTimeInterval='" + classTimeInterval + '\'' +
                ", classroomName='" + classroomName + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", subgroup=" + subgroup +
                ", teacherFullName='" + teacherFullName + '\'' +
                ", subjectType='" + subjectType + '\'' +
                //", subjectTypeAbbreviated='" + subjectTypeAbbreviated + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", subjectNameAbbreviated='" + subjectNameAbbreviated + '\'' +
                ", teacherId=" + teacherId +
                ", subjectId=" + subjectId +
                ", groupId=" + groupId +
                '}';
    }
}

