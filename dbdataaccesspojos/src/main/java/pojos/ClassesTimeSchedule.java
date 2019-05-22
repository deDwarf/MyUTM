package pojos;

public class ClassesTimeSchedule {
  private long timeScheduleId;
  private long classNumber;
  private java.sql.Time timeFrom;
  private java.sql.Time timeTo;


  public long getTimeScheduleId() {
    return timeScheduleId;
  }

  public void setTimeScheduleId(long timeScheduleId) {
    this.timeScheduleId = timeScheduleId;
  }


  public long getClassNumber() {
    return classNumber;
  }

  public void setClassNumber(long classNumber) {
    this.classNumber = classNumber;
  }


  public java.sql.Time getTimeFrom() {
    return timeFrom;
  }

  public void setTimeFrom(java.sql.Time timeFrom) {
    this.timeFrom = timeFrom;
  }


  public java.sql.Time getTimeTo() {
    return timeTo;
  }

  public void setTimeTo(java.sql.Time timeTo) {
    this.timeTo = timeTo;
  }

}
