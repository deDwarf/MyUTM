package core;

import org.testng.annotations.Test;
import pojos.Teacher;

import java.sql.SQLException;

import static org.testng.Assert.*;

public class GenericCUDServiceTest {

    GenericCUDService<Teacher> t;
    Database db;
    Teacher teacher = new Teacher();

    public GenericCUDServiceTest(GenericCUDService<Teacher> t) throws SQLException {
        db = Database.create(
                "localhost"
                , 3306
                , "FCIMApp"
                , "myfcim_read_01"
                , "MYUTM2018$"
        );

        t = new GenericCUDService<>(db, "fcimapp", "teachers", Teacher.class);
        teacher = new Teacher();
        teacher.setFirstNm("Denis");
        teacher.setSecondNm("Rimskiy");
        teacher.setPrimaryEmail("drimsk@gmail.com");
        teacher.setDepartment("IT");
    }

    @Test(priority = 1)
    public void testUpdate() throws SQLException {
        t.update((long)3, "middleNm", "Vasilievich");
        assertEquals("Vasilievich", db.getTeacher((long)3).getMiddleNm());
    }

    @Test(priority = 1)
    public void testInsert() throws SQLException {
        Long id = t.insert(teacher);
        teacher.setTeacherId(id);

        assertNotNull(id);
    }

    @Test(priority = 2)
    public void testDelete() throws SQLException {
        t.delete(teacher.getTeacherId());
        assertNull(db.getTeacher(teacher.getTeacherId()));
    }
}
