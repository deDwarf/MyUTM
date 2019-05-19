package pojos.helper;

public class EntryIdAndGroupIdAndGroupName {
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
