package fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import pojos.RegularScheduleEntry;
import pojos.ScheduleEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Expects students to be subscribed to FCM topics according to their groups.
 * Name: CIM-GROUP-{groupId}
 * or for teachers: CIM-TEACHER-{teacherId}
 *
 *
 */
public class BufferedFCMServiceImpl implements IFCMService{
    // buffer size in seconds
    private static final int BUFFER_SIZE = 10;
    private static final String GROUP_TOPIC_TEMPLATE = "CIM_GROUP_";
    private static final String TEACHER_TOPIC_TEMPLATE = "CIM_TEACHER_";

    private final FastDateFormat FDF_LONG = FastDateFormat.getInstance("EEEEE, dd/mm", null, null);
    private final FastDateFormat FDF_SHORT = FastDateFormat.getInstance("EEEEE", null, null);

    private static BufferedFCMServiceImpl inst;
    public static BufferedFCMServiceImpl getInstance() {
        if (inst == null) {
            inst = new BufferedFCMServiceImpl();
        }
        return inst;
    }

    private FirebaseMessaging fm;

    private BufferedFCMServiceImpl() {
        String propsFile = "fcimapp-firebase-adminsdk-beew3-2fde6c8b0d.json";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            File prop = new File(IOUtils.resourceToURL(propsFile, loader).toURI());
            FileInputStream serviceAccount = new FileInputStream(prop);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            this.fm = FirebaseMessaging.getInstance(FirebaseApp.getInstance());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to initialize FCM service");
        }

    }

    @Override
    public void notifyLessonCancelled(RegularScheduleEntry e) {

    }

    @Override
    public void notifyLessonCancelled(ScheduleEntry e) {

    }

    // TODO String.format() is very-very slow
    private final static String rseAddTitleTemplate = "A class has been scheduled for every%s %s(#%s)";
    private final static String rseAddBodyTemplate = "%s. %s classes will be taking place every%s %s at %s-%s";
    @Override
    public void notifyLessonRegistered(RegularScheduleEntry e) {
        String weekday = DayOfWeek.of((int)e.getWeekDay()).getDisplayName(TextStyle.FULL, Locale.US);
        Message msg = Message.builder()
                .setNotification(new Notification(
                        String.format(rseAddTitleTemplate, e.getWeekParity(), weekday, e.getClassNumber()),
                        String.format(rseAddBodyTemplate, e.getSubjectTypeAbbreviated(), e.getSubjectNameAbbreviated(),
                                e.getWeekParity(), weekday, e.getClassStartTime(), e.getClassEndTime())
                ))
                .setCondition(getTeacherOrGroupCondition(e.getTeacherId(), e.getGroupId()))
                .build();
        try {
            fm.send(msg);
        } catch (FirebaseMessagingException e1) {
            e1.printStackTrace();
        }
    }

    private final static String seAddTitleTemplate = "Teacher <%s> has scheduled a class for %s";
    private final static String seAddBodyTemplate = "'%s' class will take place at %s, %s-%s(#%s)";
    @Override
    public void notifyLessonRegistered(ScheduleEntry e) {
        String teacherDisplayName = abbreviateTeacherName(e.getTeacherFirstName(), e.getTeacherSecondName(), e.getTeacherMiddleName());
        Message msg = Message.builder()
                .setNotification(new Notification(
                        String.format(seAddTitleTemplate, teacherDisplayName, FDF_LONG.format(e.getDate())),
                        String.format(seAddBodyTemplate, e.getSubjectName(),FDF_SHORT.format(e.getDate()),
                                e.getClassStartTime(), e.getClassEndTime(), e.getClassNumber())
                ))
                .setTopic(GROUP_TOPIC_TEMPLATE + e.getGroupId())
                .build();
        try {
            fm.send(msg);
        } catch (FirebaseMessagingException e1) {
            e1.printStackTrace();
        }
    }


    private String abbreviateTeacherName(String firstNm, String secondNm, String middleNm) {
        StringBuilder bldr = new StringBuilder();
        bldr.append(StringUtils.capitalize(secondNm));
        if (firstNm != null) {
            bldr.append(Character.toUpperCase(firstNm.charAt(0))).append('.');
        }
        if (middleNm != null) {
            bldr.append(Character.toUpperCase(middleNm.charAt(0))).append('.');
        }
        return bldr.toString();
    }

    private String getTeacherOrGroupCondition(long teacherId, long groupId) {
        return "'" + GROUP_TOPIC_TEMPLATE + groupId + "' in topics || '" +
                TEACHER_TOPIC_TEMPLATE + teacherId + "' in topics";
    }
}
