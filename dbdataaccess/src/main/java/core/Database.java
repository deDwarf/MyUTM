package core;

import authpojos.Account;
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

public class Database {
    private static final RowProcessor rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private DataSource src;
    private QueryRunner runner;

    private final String getTeacherScheduleForDayTemplateSQL;
    private final String getGroupScheduleForDayTemplateSQL;
    private final String getStudentByEmail;
    private final String getStudentById;

    private Database() {
        this.getGroupScheduleForDayTemplateSQL = readSQLFromResources(
                "select_schedule_by_date_and_group.sql");
        this.getTeacherScheduleForDayTemplateSQL = readSQLFromResources(
                "select_schedule_by_date_and_teacher.sql");
        this.getStudentByEmail = readSQLFromResources(
                "select_student_by_email.sql");
        this.getStudentById = readSQLFromResources(
                "select_student_by_email.sql");
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

    public Teacher getTeacher(int teacherId) throws SQLException {
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

    public List<Classroom> getClassrooms() throws SQLException {
        final ResultSetHandler<List<Classroom>> h = new BeanListHandler<>(Classroom.class, rowProcessor);
        return runner.query("select * from fcimapp.Classrooms", h);
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

    public List<ScheduleEntry> getTeacherScheduleForDay(java.util.Date day, Long teacherId) throws SQLException {
        return getScheduleEntries(day, teacherId, getTeacherScheduleForDayTemplateSQL);
    }

    public List<ScheduleEntry> getGroupScheduleForDay(java.util.Date day, Long groupId) throws SQLException {
        return getScheduleEntries(day, groupId, getGroupScheduleForDayTemplateSQL);
    }

    private List<ScheduleEntry> getScheduleEntries(java.util.Date day, Long id, String getScheduleForDayTemplateSQL)
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
