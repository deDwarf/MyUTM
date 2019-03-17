package pojos;


public class Teacher {

  private long teacherId;
  private String firstNm;
  private String middleNm;
  private String secondNm;
  private String photo;
  private String primaryEmail;
  private String secondaryEmail;
  private String phone;
  private String department;
  private String position;


  public long getTeacherId() {
    return teacherId;
  }

  public void setTeacherId(long teacherId) {
    this.teacherId = teacherId;
  }


  public String getFirstNm() {
    return firstNm;
  }

  public void setFirstNm(String firstNm) {
    this.firstNm = firstNm;
  }


  public String getMiddleNm() {
    return middleNm;
  }

  public void setMiddleNm(String middleNm) {
    this.middleNm = middleNm;
  }


  public String getSecondNm() {
    return secondNm;
  }

  public void setSecondNm(String secondNm) {
    this.secondNm = secondNm;
  }


  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }


  public String getPrimaryEmail() {
    return primaryEmail;
  }

  public void setPrimaryEmail(String primaryEmail) {
    this.primaryEmail = primaryEmail;
  }


  public String getSecondaryEmail() {
    return secondaryEmail;
  }

  public void setSecondaryEmail(String secondaryEmail) {
    this.secondaryEmail = secondaryEmail;
  }


  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }


  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }


  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @Override
  public String toString() {
    return "Teacher{" +
            "teacherId=" + teacherId +
            ", firstNm='" + firstNm + '\'' +
            ", middleNm='" + middleNm + '\'' +
            ", secondNm='" + secondNm + '\'' +
            ", primaryEmail='" + primaryEmail + '\'' +
            '}';
  }
}
