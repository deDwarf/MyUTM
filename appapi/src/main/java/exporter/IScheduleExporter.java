package exporter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IScheduleExporter {
    File exportStudentSchedule(List<Long> ids) throws SQLException;

    File exportTeacherSchedule(List<Long> ids) throws SQLException;
}