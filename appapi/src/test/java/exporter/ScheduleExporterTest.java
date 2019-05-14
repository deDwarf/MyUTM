package exporter;

import core.AppContext;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import static org.testng.Assert.*;

public class ScheduleExporterTest {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testExportStudentSchedule() throws Exception {
        AppContext.getInstance().loadDatabaseController("db-remote.properties");

        ScheduleExporter e = new ScheduleExporter();
        File f = e.exportStudentSchedule(Arrays.asList((long) 1, (long) 2));
        System.out.println(f.getAbsolutePath());
    }

    @Test
    public void testExportTeacherSchedule() throws Exception {
        AppContext.getInstance().loadDatabaseController("db-remote.properties");

        ScheduleExporter e = new ScheduleExporter();
        File f = e.exportTeacherSchedule(Arrays.asList((long) 1, (long) 2));
        System.out.println(f.getAbsolutePath());
    }

    @DataProvider
    public Object[][] provide() {
        return new Object[][] {
                new Object[] {0, 1, 10, 4},
                new Object[] {0, 5, 34, 4},
                new Object[] {1, 2, 59, 4},
        };
    }

    @Test(dataProvider = "provide")
    public void testGetStartCellAddr(int row, int col, int expectedTemplateRow, int expectedTemplateCol)
                throws Exception {
        CellAddress addr = ScheduleExporter.TemplateConstant.getStartCellAddr(row, col);
        log.info("Given: <{}, {}> calculated: <{}, {}>", row, col, addr.getRow(), addr.getColumn());
        assertEquals(addr.getColumn(), expectedTemplateCol);
        assertEquals(addr.getRow(), expectedTemplateRow);
    }
}