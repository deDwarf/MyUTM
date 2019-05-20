package core;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pojos.Teacher;

import java.math.BigInteger;
import java.sql.SQLException;

import static org.testng.Assert.*;

@Test
public class GenericCUDServiceTest {
    GenericCUDService<Teacher> t;
    Database db;
    Teacher teacher;

    @BeforeTest
    public void init() throws SQLException {
        db = Database.create(
                "localhost"
                , 3306
                , "FCIMApp"
                , "myfcim_write"
                , "MYUTMwrite2018$"
        );

        this.t = new GenericCUDService<>(db, "fcimapp", "teachers", Teacher.class);
        teacher = new Teacher();
        teacher.setFirstNm("Denis");
        teacher.setSecondNm("Rimskiy");
        teacher.setPrimaryEmail("drimsk@gmail.com");
        teacher.setDepartment("IT");
    }

    @Test
    public void testUpdate() throws SQLException {
        t.update((long)3, "middleNm", "Vasilievich");
        assertEquals(db.getTeacher((long)3).getMiddleNm(), "Vasilievich");
    }

    @Test
    public void testInsert() {
        BigInteger id = null;
        try {
            id = t.insert(teacher);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertNotNull(id);
        teacher.setTeacherId(id.longValue());
    }

    @Test(priority = 1)
    public void testDelete() throws SQLException {
        t.delete(teacher.getTeacherId());
        assertNull(db.getTeacher(teacher.getTeacherId()));
    }

    @Test
    public void testHello() {
        System.out.println("hello");
    }
}
