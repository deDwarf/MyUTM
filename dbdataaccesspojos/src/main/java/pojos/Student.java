package pojos;


public class Student {

  private long studentId;
  private long groupId;
  private String groupNumber;
  private String firstNm;
  private String secondNm;
  private String internalEmailAddress;
  private String personalEmailAddress;
  private String avatar;

  public long getStudentId() {
    return studentId;
  }

  public void setStudentId(long studentId) {
    this.studentId = studentId;
  }


  public long getGroupId() {
    return groupId;
  }

  public void setGroupId(long groupId) {
    this.groupId = groupId;
  }


  public String getFirstNm() {
    return firstNm;
  }

  public void setFirstNm(String firstNm) {
    this.firstNm = firstNm;
  }


  public String getSecondNm() {
    return secondNm;
  }

  public void setSecondNm(String secondNm) {
    this.secondNm = secondNm;
  }


  public String getInternalEmailAddress() {
    return internalEmailAddress;
  }

  public void setInternalEmailAddress(String internalEmailAddress) {
    this.internalEmailAddress = internalEmailAddress;
  }


  public String getPersonalEmailAddress() {
    return personalEmailAddress;
  }

  public void setPersonalEmailAddress(String personalEmailAddress) {
    this.personalEmailAddress = personalEmailAddress;
  }


  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }


  public String getGroupNumber() {
    return groupNumber;
  }

  public void setGroupNumber(String groupNumber) {
    this.groupNumber = groupNumber;
  }

  @Override
  public String toString() {
    return "Student{" +
            " studentId=" + studentId +
            ", groupId=" + groupId +
            ", groupNumber=" + groupNumber +
            ", firstNm='" + firstNm + '\'' +
            ", secondNm='" + secondNm + '\'' +
            ", personalEmailAddress='" + personalEmailAddress + '\'' +
            '}';
  }
}
