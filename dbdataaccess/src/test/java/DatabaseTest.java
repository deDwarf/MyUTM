import core.Database;
import core.InspectionsService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojos.GroupedScheduleEntry;
import pojos.ScheduleEntry;
import pojos.Teacher;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DatabaseTest {
    private Database db;
    private InspectionsService insp;
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    @BeforeClass
    public void setDB() throws SQLException {
        db = Database.create(
                "localhost"
                , 3306
                , "FCIMApp"
                , "myfcim_read_01"
                , "MYUTM2018$"
        );
        insp = new InspectionsService(db);
    }

    @Test
    public void testGetTeacherGroupedScheduleForDay() throws SQLException, ParseException {
        List<GroupedScheduleEntry> ts = db.getTeacherGroupedScheduleForDay(
                df.parse("2019-03-18"), df.parse("2019-03-18"), (long)1);
        System.out.println(ts.get(0).getGroupedInfo());
    }

    @Test
    public void testGetTeachers() throws SQLException {
        List<Teacher> ts = db.getTeachers();
        System.out.println(ts);
    }

    @Test
    public void testGetSchedule() throws ParseException, SQLException {
        List<ScheduleEntry> e = db.getGroupScheduleForDay(
                df.parse("2019-03-18"), df.parse("2019-03-18"), (long)1);
        System.out.println(e);
    }

    @Test
    public void testInspections() throws ParseException, SQLException {
        insp.runOneTeacherManyClassroomsInspection();
    }
}
