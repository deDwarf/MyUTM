package pojos;


public class Group {

  private long groupId;
  private String groupName;
  private String speciality;
  private String faculcy;


  public long getGroupId() {
    return groupId;
  }

  public void setGroupId(long groupId) {
    this.groupId = groupId;
  }


  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }


  public String getSpeciality() {
    return speciality;
  }

  public void setSpeciality(String speciality) {
    this.speciality = speciality;
  }


  public String getFaculcy() {
    return faculcy;
  }

  public void setFaculcy(String faculcy) {
    this.faculcy = faculcy;
  }

  @Override
  public String toString() {
    return "Group{" +
            "groupId=" + groupId +
            ", groupName='" + groupName + '\'' +
            ", speciality='" + speciality + '\'' +
            ", faculcy='" + faculcy + '\'' +
            '}';
  }
}
