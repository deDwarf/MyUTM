package core;

import authpojos.Account;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojos.*;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Database {
    private static final RowProcessor rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private DataSource src;
    private QueryRunner runner;

    private final String getTeacherScheduleForDayTemplateSQL;
    private final String getGroupScheduleForDayTemplateSQL;

    private Database() {
        this.getGroupScheduleForDayTemplateSQL = readSQLFromResources(
                "select_schedule_by_date_and_group.sql");
        this.getTeacherScheduleForDayTemplateSQL = readSQLFromResources(
                "select_schedule_by_date_and_teacher.sql");
    }

    public static Database create(String hostname, int port, String database,
                                  String login, String password) throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(hostname);
        ds.setPort(port);
        ds.setDatabaseName(database);
        ds.setUser(login);
        ds.setPassword(password);
        ds.setServerTimezone("Europe/Chisinau");

        Database db = new Database();
        db.src = ds;
        db.runner = new QueryRunner(db.src);

        return db;
    }


    public List<Teacher> getTeachers() throws SQLException {
        final ResultSetHandler<List<Teacher>> h = new BeanListHandler<>(Teacher.class, rowProcessor);
        return runner.query("select * from fcimapp.Teachers", h);
    }

    public List<Subject> getSubjects() throws SQLException {
        final ResultSetHandler<List<Subject>> h = new BeanListHandler<>(Subject.class, rowProcessor);
        return runner.query("select * from fcimapp.Subjects", h);
    }

    public List<Classroom> getClassrooms() throws SQLException {
        final ResultSetHandler<List<Classroom>> h = new BeanListHandler<>(Classroom.class, rowProcessor);
        return runner.query("select * from fcimapp.Classrooms", h);
    }

    public List<Group> getGroups() throws SQLException {
        final ResultSetHandler<List<Group>> h = new BeanListHandler<>(Group.class, rowProcessor);
        return runner.query("select * from fcimapp.Groups", h);
    }

    public List<String> getGroupNamesOnly() throws SQLException {
        final ResultSetHandler<List<String>> h = new BeanListHandler<>(String.class, rowProcessor);
        return runner.query("select group_name from fcimapp.Groups", h);
    }

    public List<ScheduleEntry> getTeacherScheduleForDay(java.util.Date day, int teacherId) throws SQLException {
        return getScheduleEntries(day, teacherId, getTeacherScheduleForDayTemplateSQL);
    }

    public List<ScheduleEntry> getGroupScheduleForDay(java.util.Date day, int groupId) throws SQLException {
        return getScheduleEntries(day, groupId, getGroupScheduleForDayTemplateSQL);
    }

    private List<ScheduleEntry> getScheduleEntries(java.util.Date day, int id, String getScheduleForDayTemplateSQL)
            throws SQLException {
        final ResultSetHandler<List<ScheduleEntry>> h = new BeanListHandler<>(ScheduleEntry.class, rowProcessor);
        String date = formatter.format(day);
        return runner.query(getScheduleForDayTemplateSQL, h, id, date, id, date);
    }

    private static String readSQLFromResources(String fileName) {
        try {
            return IOUtils.resourceToString(fileName, Charset.forName("UTF-8"), Database.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from given file: " + fileName, e);
        }
    }

    // --- functions

    public void getFreeClassroomsForDateAndTime() {}

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
        return runner.query("SELECT * FROM FCIMApp.accounts WHERE username = ?", h, username);
    }

    public int createUserAccount(String username, String passwordHash, String userRole) throws SQLException {
        final ResultSetHandler<Integer> rh = new ScalarHandler<>();
        Integer id = runner.insert("INSERT INTO FCIMApp.accounts " +
                        "(user_login, user_role, password_hash, registered_date) " +
                        "VALUES (?, ?, ?, ?)", rh,
                        username, userRole, passwordHash, Calendar.getInstance().getTime());

        return id;
    }

    public int createStudent(String userEmail, String firstNm, String secondNm, int groupId) throws SQLException {
        final ResultSetHandler<Integer> rh = new ScalarHandler<>();
        Integer id = runner.insert("INSERT INTO FCIMApp.Students (group_id, first_nm, second_nm, " +
                        "internal_email_address) " +
                        "VALUES (?, ?, ?, ?)", rh,
                groupId, firstNm, secondNm, userEmail);

        return id;
    }

    public void linkStudentWithAccount(int studentId, int accountId) throws SQLException {
        runner.update("UPDATE FCIMApp.Students SET account_id = ? WHERE teacherId = ?", accountId, studentId);
    }

    public void linkTeacherWithAccount(int teacherId, int accountId) throws SQLException {
        runner.update("UPDATE FCIMApp.Teachers SET account_id = ? WHERE teacherId = ?", accountId, teacherId);
    }
}
