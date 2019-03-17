package core;

import authpojos.Account;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import pojos.Classroom;
import pojos.Group;
import pojos.Subject;
import pojos.Teacher;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class Database {
    private final RowProcessor rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());
    private DataSource src;
    private QueryRunner runner;
    private Database() {}

    public static Database create(String hostname, int port, String database,
                                  String login, String password) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(hostname);
        ds.setPort(port);
        ds.setDatabaseName(database);
        ds.setUser(login);
        ds.setPassword(password);

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

    public void getTeacherScheduleForDay() {

    }

    public void getGroupScheduleForDay() {

    }

    private String readSQLFromResource(String resourceId) {
        return null;
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
