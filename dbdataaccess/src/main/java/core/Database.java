package core;

import pojos.Account;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.io.IOUtils;
import pojos.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

// TODO make subject type id non-null

public class Database {
    protected static final RowProcessor rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());
    protected static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    protected DataSource src;
    protected QueryRunner runner;

    private final String getDatedScheduleEntryById;
    private final String getFreeTime;
    private final String getGroupedScheduleSQL;
    private final String getFreeClassrooms;
    private final String getScheduleForDayTemplateSQL;
    private final String getStudentByEmail;
    private final String getStudentById;
    private final String getGroupedRegularSchedule;

    private Database() {
        this.getScheduleForDayTemplateSQL = readSQLFromResources(
                "select_schedule_for_date.sql");
        this.getDatedScheduleEntryById = readSQLFromResources(
                "select_schedule_by_id.sql");
        this.getStudentByEmail = readSQLFromResources(
                "select_student_by_email.sql");
        this.getStudentById = readSQLFromResources(
                "select_student_by_id.sql");
        this.getFreeClassrooms = readSQLFromResources(
                "select_free_classrooms_for_time.sql");
        this.getFreeTime = readSQLFromResources(
                "select_free_time_for_teacher_group_date.sql");
        this.getGroupedRegularSchedule = readSQLFromResources(
                "select_schedule_grouped_regular.sql");
        this.getGroupedScheduleSQL = readSQLFromResources(
                "select_schedule_grouped_for_date.sql");
    }

    protected static String readSQLFromResources(String fileName) {
        try {
            return IOUtils.resourceToString(fileName, Charset.forName("UTF-8"), Database.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from given file: " + fileName, e);
        }
    }

    public static Database create(String hostname, int port, String database,
                                  String login, String password) throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(hostname);
        ds.setPort(port);
        ds.setDatabaseName(database);
        ds.setUser(login);
        ds.setPassword(password);
        ds.setServerTimezone("UTC");

        Database db = new Database();
        db.src = ds;
        db.runner = new QueryRunner(db.src);

        return db;
    }


    public void updateStudentParam(Long studentId, String paramName, Object paramValue) throws SQLException {
        runner.update(String.format("UPDATE fcimapp.Students SET `%s` = ? WHERE student_id = ?"
                , paramName), paramValue, studentId);
    }

    public Student getStudent(String username) throws SQLException {
        final ResultSetHandler<Student> rh = new BeanHandler<>(Student.class, rowProcessor);
        return runner.query(getStudentByEmail, rh, username);
    }

    public Student getStudent(Long id) throws SQLException {
        final ResultSetHandler<Student> rh = new BeanHandler<>(Student.class, rowProcessor);
        return runner.query(getStudentById, rh, id);
    }


    public List<Teacher> getTeachers() throws SQLException {
        final ResultSetHandler<List<Teacher>> h = new BeanListHandler<>(Teacher.class, rowProcessor);
        return runner.query("select * from fcimapp.Teachers", h);
    }

    public List<TeacherWithAccountInfo> getTeachersWithAI() throws SQLException {
        final ResultSetHandler<List<TeacherWithAccountInfo>> h = new BeanListHandler<>(TeacherWithAccountInfo.class, rowProcessor);
        return runner.query("select * from fcimapp.Teachers", h);
    }

    public Teacher getTeacher(Long teacherId) throws SQLException {
        final ResultSetHandler<Teacher> h = new BeanHandler<>(Teacher.class, rowProcessor);
        return runner.query("select * from fcimapp.Teachers where teacher_id = ?", h, teacherId);
    }

    public Teacher getTeacher(String email) throws SQLException {
        if (email == null) {
            return null;
        }
        final ResultSetHandler<Teacher> h = new BeanHandler<>(Teacher.class, rowProcessor);
        return runner.query("select * from fcimapp.Teachers where primary_email = ?", h, email.toLowerCase());
    }


    public List<Subject> getSubjects() throws SQLException {
        final ResultSetHandler<List<Subject>> h = new BeanListHandler<>(Subject.class, rowProcessor);
        return runner.query("select * from fcimapp.Subjects", h);
    }

    public Subject getSubject(Long id) throws SQLException {
        final ResultSetHandler<Subject> h = new BeanHandler<>(Subject.class, rowProcessor);
        return runner.query("select * from fcimapp.Subjects where subject_id = ?", h, id);
    }

    public List<SubjectType> getSubjectTypes() throws SQLException {
        final ResultSetHandler<List<SubjectType>> h = new BeanListHandler<>(SubjectType.class, rowProcessor);
        return runner.query("select * from fcimapp.Subject_Types", h);
    }

    public SubjectType getSubjectType(Long id) throws SQLException {
        final ResultSetHandler<SubjectType> h = new BeanHandler<>(SubjectType.class, rowProcessor);
        return runner.query("select * from fcimapp.Subject_Types where subject_type_id = ?", h, id);
    }


    public List<Classroom> getClassrooms() throws SQLException {
        final ResultSetHandler<List<Classroom>> h = new BeanListHandler<>(Classroom.class, rowProcessor);
        return runner.query("select * from fcimapp.Classrooms", h);
    }

    public Classroom getClassroom(Long id) throws SQLException {
        final ResultSetHandler<Classroom> h = new BeanHandler<>(Classroom.class, rowProcessor);
        return runner.query("select * from fcimapp.Classrooms where classroom_id = ?", h, id);
    }


    public List<Group> getGroups() throws SQLException {
        final ResultSetHandler<List<Group>> h = new BeanListHandler<>(Group.class, rowProcessor);
        return runner.query("select * from fcimapp.Groups", h);
    }

    public Group getGroupByName(String name) throws SQLException {
        name = name.toUpperCase().trim();
        final ResultSetHandler<Group> h = new BeanHandler<>(Group.class, rowProcessor);
        return runner.query("select * from fcimapp.Groups where group_name = ?", h, name);
    }

    public Group getGroupById(Long id) throws SQLException {
        final ResultSetHandler<Group> h = new BeanHandler<>(Group.class, rowProcessor);
        return runner.query("select * from fcimapp.Groups where group_id = ?", h, id);
    }

    public List<String> getGroupNamesOnly() throws SQLException {
        final ColumnListHandler<String> h = new ColumnListHandler<>(1);
        return runner.query("select distinct `group_name` from fcimapp.Groups", h);
    }


    public ScheduleEntry getDatedScheduleEntry(Long id) throws SQLException {
        final ResultSetHandler<ScheduleEntry> h = new BeanHandler<>(ScheduleEntry.class, rowProcessor);
        return runner.query(this.getDatedScheduleEntryById, h, id);
    }

    public List<ScheduleEntry> getTeacherScheduleForDay(java.util.Date day, java.util.Date tillDay,
                                                        Long teacherId) throws SQLException {
        return getScheduleEntries(day, tillDay, (long) -1, teacherId);
    }

    public List<ScheduleEntry> getGroupScheduleForDay(java.util.Date day, java.util.Date tillDay,
                                                      Long groupId) throws SQLException {
        return getScheduleEntries(day, tillDay, groupId, (long) -1);
    }

    @SuppressWarnings("Duplicates")
    private List<ScheduleEntry> getScheduleEntries(java.util.Date day, java.util.Date tillDay,
                                                   Long groupId, Long teacherId)
            throws SQLException {
        final ResultSetHandler<List<ScheduleEntry>> h = new BeanListHandler<>(ScheduleEntry.class, rowProcessor);
        String fromDate = formatter.format(day);
        String tillDate = formatter.format(tillDay);
        return runner.query(getScheduleForDayTemplateSQL, h,
                groupId, teacherId, fromDate, tillDate,
                groupId, teacherId, fromDate, tillDate);
    }

    public List<GroupedScheduleEntry> getTeacherGroupedScheduleForDay(java.util.Date day, java.util.Date tillDay,
                                                                      Long teacherId) throws SQLException {
        return getGroupedScheduleEntries(day, tillDay, (long) -1, teacherId);
    }

    public List<GroupedScheduleEntry> getGroupedGroupScheduleForDay(java.util.Date day, java.util.Date tillDay,
                                                                    Long groupId) throws SQLException {
        return getGroupedScheduleEntries(day, tillDay, groupId, (long) -1);
    }

    @SuppressWarnings("Duplicates")
    private List<GroupedScheduleEntry> getGroupedScheduleEntries(java.util.Date day, java.util.Date tillDay,
                                                                 Long groupId, Long teacherId)
            throws SQLException {
        final ResultSetHandler<List<GroupedScheduleEntry>> h = new BeanListHandler<>(GroupedScheduleEntry.class, rowProcessor);
        String fromDate = formatter.format(day);
        String tillDate = formatter.format(tillDay);
        return runner.query(getGroupedScheduleSQL, h,
                groupId, teacherId, fromDate, tillDate,
                groupId, teacherId, fromDate, tillDate);
    }

    public List<RegularScheduleEntry> getRegularSchedule(Long groupId)
            throws SQLException {
        final ResultSetHandler<List<RegularScheduleEntry>> h = new BeanListHandler<>(RegularScheduleEntry.class, rowProcessor);
        return runner.query("select * from fcimapp.vw_denormalized_regular_schedule" +
                " where group_id = ?" +
                " order by group_number, week_day, class_number, week_parity", h, groupId);
    }

    public List<GroupedRegularScheduleEntry> getGroupedRegularScheduleByGroup(Long groupId)
            throws SQLException {
        final ResultSetHandler<List<GroupedRegularScheduleEntry>> h = new BeanListHandler<>(GroupedRegularScheduleEntry.class, rowProcessor);
        return runner.query(this.getGroupedRegularSchedule, h, groupId, -1);
    }

    public List<GroupedRegularScheduleEntry> getGroupedRegularScheduleByTeacher(Long teacherId)
            throws SQLException {
        final ResultSetHandler<List<GroupedRegularScheduleEntry>> h = new BeanListHandler<>(GroupedRegularScheduleEntry.class, rowProcessor);
        return runner.query(this.getGroupedRegularSchedule, h, -1, teacherId);
    }


    // --- register schedule
    public RegularScheduleEntry registerRegularLesson(String table, RegularScheduleEntry data) throws SQLException {
        final ResultSetHandler<BigInteger> h = new ScalarHandler<>();
        final String template =
                "INSERT INTO %s (group_id, week_parity, week_day, class_number, classroom_id, " +
                        "main_teacher_id, subject_id, subject_type, subgroup, semester_id)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        BigInteger id = runner.insert(String.format(template, table), h,
                data.getGroupId(),
                data.getWeekParity(), data.getWeekDay(),
                data.getClassNumber(), data.getClassroomId(),
                data.getTeacherId(), data.getSubjectId(), data.getSubjectTypeId(),
                data.getSubgroup(), 1 // TODO !!!!!!!!
                );

        return getRegularScheduleEntry(id);
    }

    public int removeRegularClasses(List<Integer> ids) throws SQLException {
        if (ids == null || ids.size() == 0) {
            return 0;
        }

        StringBuilder result = new StringBuilder();
        result.append(ids.get(0));
        for (int i = 1; i < ids.size(); i++) {
            result.append(",").append(ids.get(i));
        }
        return runner.update(String.format("delete from fcimapp.schedule where entry_id in (%s)", result.toString()));
    }

    private RegularScheduleEntry getRegularScheduleEntry(BigInteger scheduleEntryId)
            throws SQLException {
        final ResultSetHandler<RegularScheduleEntry> h = new BeanHandler<>(RegularScheduleEntry.class, rowProcessor);
        return runner.query("select * from fcimapp.vw_denormalized_regular_schedule" +
                " where schedule_entry_id = ?" +
                " order by group_number, week_day, class_number, week_parity", h, scheduleEntryId);
    }


    public Long registerDatedClass(ScheduleEntry e) throws SQLException {
        final ResultSetHandler<BigInteger> h = new ScalarHandler<>();
        return runner.insert("INSERT INTO fcimapp.schedule_dated_classes " +
                "(group_id, date_key, class_number, classroom_id, main_teacher_id, subject_id, subject_type) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)", h, e.getGroupId(), e.getDate(), e.getClassNumber(), e.getClassroomId(),
                e.getTeacherId(), e.getSubjectId(), e.getSubjectTypeId()).longValue();
    }

    public void cancelRegularClasses(Date date, List<Long> ids) throws SQLException {
        final ResultSetHandler<BigInteger> h = new ScalarHandler<>();
        Object[][] batchParams = new Object[ids.size()][2];
        for (int i = 0; i < ids.size(); i++) {
            batchParams[0][i] = ids.get(i);
            batchParams[1][i] = date;
        }
        runner.insertBatch("INSERT INTO fcimapp.schedule_cancelled_classes" +
                "(schedule_id, cancelled_for_date) VALUES(?, ?)", h, batchParams);
    }

    public void cancelDatedClasses(List<Long> ids) throws SQLException {
        final ResultSetHandler<BigInteger> h = new ScalarHandler<>();
        if (ids == null || ids.isEmpty()) {
            return;
        }
        StringBuilder bldr = new StringBuilder("DELETE FROM fcimapp.schedule_dated_classes WHERE entry_id in (?");
        for (int i = 1; i < ids.size(); i++) {
            bldr.append(", ?");
        }

        runner.update(bldr.append(")").toString(), h, ids.toArray());
    }

    // --- functions

    public List<Classroom> getFreeClassroomsForDateAndTime(java.util.Date date, Long classNumber) throws SQLException {
        final BeanListHandler<Classroom> h = new BeanListHandler<>(Classroom.class, rowProcessor);
        return runner.query(getFreeClassrooms, h, formatter.format(date), classNumber);
    }

    public List<ClassesTimeSchedule> getFreeTimeForDateAndTeacherAndGroup(java.util.Date date,
                                                                Long groupId,
                                                                Long teacherId) throws SQLException {
        final BeanListHandler<ClassesTimeSchedule> h = new BeanListHandler<>(ClassesTimeSchedule.class, rowProcessor);
        return runner.query(getFreeTime, h, formatter.format(date), groupId, teacherId);
    }

    public void getFreeTimeForGroupAndDay(long groupId, Date date) {}

    public void getFreeTimeForGroupsAndDay(List<Long> groupIds, Date date) {}

    public void getFreeTimeForTeacherAndDay(long teacherId, Date date) {}

    public void getFreeTimeForTeacherAndGroupsAndDay(long teacherId, List<Long> groupIds, Date date) {

    }

    // ---- inspections

    public void checkClassroomIsAvailableAtTime() {}

    public void checkTeacherIsAvailableAtTime() {}

    public void checkGroupIsAvailableAtTime() {}

    // -- login

    public Account getAccountByUsername(String username) throws SQLException {
        final ResultSetHandler<Account> h = new BeanHandler<>(Account.class, rowProcessor);
        return runner.query("SELECT * FROM FCIMApp.accounts WHERE user_login = ?", h, username);
    }

    public BigInteger createUserAccount(String username, String passwordHash, String userRole) throws SQLException {
        final ResultSetHandler<BigInteger> rh = new ScalarHandler<>();
        BigInteger id = runner.insert("INSERT INTO FCIMApp.accounts " +
                        "(user_login, user_role, password_hash, registered_date) " +
                        "VALUES (?, ?, ?, ?)", rh,
                        username, userRole, passwordHash, Calendar.getInstance().getTime());

        return id;
    }

    public BigInteger createStudent(String userEmail, String firstNm, String secondNm, Integer groupId) throws SQLException {
        final ResultSetHandler<BigInteger> rh = new ScalarHandler<>();
        BigInteger id = runner.insert("INSERT INTO FCIMApp.Students (group_id, first_nm, second_nm, " +
                        "internal_email_address) " +
                        "VALUES (?, ?, ?, ?)", rh,
                groupId, firstNm, secondNm, userEmail);

        return id;
    }

    public void linkStudentWithAccount(BigInteger studentId, BigInteger accountId) throws SQLException {
        runner.update("UPDATE FCIMApp.Students SET account_id = ? WHERE student_id = ?", accountId, studentId);
    }

    public void linkTeacherWithAccount(BigInteger teacherId, BigInteger accountId) throws SQLException {
        runner.update("UPDATE FCIMApp.Teachers SET account_id = ? WHERE teacher_id = ?", accountId, teacherId);
    }
}
