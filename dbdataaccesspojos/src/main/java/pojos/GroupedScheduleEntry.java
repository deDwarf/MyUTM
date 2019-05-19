package pojos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import pojos.helper.EntryIdAndGroupIdAndGroupName;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class GroupedScheduleEntry {
    private static transient Gson gson = new GsonBuilder().create();
    private static Type tt = new TypeToken<List<EntryIdAndGroupIdAndGroupName>>(){}.getType();

    private List<EntryIdAndGroupIdAndGroupName> groupIdToEntryIdAndGroupNumber;
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

    public void setGroupIdToEntryIdAndGroupNumber(String groupIdToEntryIdAndGroupNumber) {
        this.groupIdToEntryIdAndGroupNumber = gson.fromJson(groupIdToEntryIdAndGroupNumber, tt);
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

