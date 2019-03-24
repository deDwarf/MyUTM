package pojos;


public class Classroom {

  private long classroomId;
  private String name;
  private long capacity;
  private String type;
  private String buildingName;
  private long floor;
  private long availability;
  private String nonAvailabilityCause;
  private long conditionRate;


  public long getClassroomId() {
    return classroomId;
  }

  public void setClassroomId(long classroomId) {
    this.classroomId = classroomId;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public long getCapacity() {
    return capacity;
  }

  public void setCapacity(long capacity) {
    this.capacity = capacity;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getBuildingName() {
    return buildingName;
  }

  public void setBuildingName(String buildingName) {
    this.buildingName = buildingName;
  }


  public long getFloor() {
    return floor;
  }

  public void setFloor(long floor) {
    this.floor = floor;
  }


  public long getAvailability() {
    return availability;
  }

  public void setAvailability(long availability) {
    this.availability = availability;
  }


  public String getNonAvailabilityCause() {
    return nonAvailabilityCause;
  }

  public void setNonAvailabilityCause(String nonAvailabilityCause) {
    this.nonAvailabilityCause = nonAvailabilityCause;
  }


  public long getConditionRate() {
    return conditionRate;
  }

  public void setConditionRate(long conditionRate) {
    this.conditionRate = conditionRate;
  }

  @Override
  public String toString() {
    return "Classroom{" +
            "classroomId=" + classroomId +
            ", name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", availability=" + availability +
            '}';
  }
}
