package pojos;


public class Subject {

  private long subjectId;
  private String subjectName;
  private String subjectNameAbbreviated;
  private String subjectDescription;


  public long getSubjectId() {
    return subjectId;
  }

  public void setSubjectId(long subjectId) {
    this.subjectId = subjectId;
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


  public String getSubjectDescription() {
    return subjectDescription;
  }

  public void setSubjectDescription(String subjectDescription) {
    this.subjectDescription = subjectDescription;
  }


  @Override
  public String toString() {
    return "Subject{" +
            "subjectId=" + subjectId +
            ", subjectName='" + subjectName + '\'' +
            '}';
  }
}
