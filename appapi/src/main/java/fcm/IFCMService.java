package fcm;

import pojos.RegularScheduleEntry;
import pojos.ScheduleEntry;

public interface IFCMService {
    void notifyLessonCancelled(RegularScheduleEntry e);

    void notifyLessonCancelled(ScheduleEntry e);

    void notifyLessonRegistered(RegularScheduleEntry e);

    void notifyLessonRegistered(ScheduleEntry e);
}
