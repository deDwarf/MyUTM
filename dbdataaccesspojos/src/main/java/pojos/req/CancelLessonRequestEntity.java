package pojos.req;

import pojos.helper.EntryIdAndGroupIdAndGroupName;

import java.sql.Date;
import java.util.List;

public class CancelLessonRequestEntity {
    private List<EntryIdAndGroupIdAndGroupName> ids;
    private Date date;

    public List<EntryIdAndGroupIdAndGroupName> getIds() {
        return ids;
    }

    public void setIds(List<EntryIdAndGroupIdAndGroupName> ids) {
        this.ids = ids;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
