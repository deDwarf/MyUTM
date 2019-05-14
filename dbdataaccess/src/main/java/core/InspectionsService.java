package core;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import pojos.RegularScheduleEntry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InspectionsService {
    private final String oneClassroomManySubjectsQuery;
    private final String oneTeacherManyClassroomQuery;
    private final String oneClassroomManyTeachersQuery;
    private Database db;

    public InspectionsService(Database db) {
        this.db = db;
        this.oneTeacherManyClassroomQuery = Database.readSQLFromResources("select_one_teacher_many_classroom.sql");
        this.oneClassroomManySubjectsQuery = Database.readSQLFromResources("select_one_classroom_many_subject.sql");
        this.oneClassroomManyTeachersQuery = Database.readSQLFromResources("select_one_classroom_many_teachers.sql");
    }

    public List<List<RegularScheduleEntry>> runOneTeacherManyClassroomsInspection() throws SQLException {
        return runGenericInspection(oneTeacherManyClassroomQuery);
    }

    public List<List<RegularScheduleEntry>> runOneClassroomManyTeachersInspection() throws SQLException {
        return runGenericInspection(oneClassroomManyTeachersQuery);
    }

    public List<List<RegularScheduleEntry>> runOneClassroomManySubjectsInspection() throws SQLException {
        return runGenericInspection(oneClassroomManySubjectsQuery);
    }

    private List<List<RegularScheduleEntry>> runGenericInspection(String query) throws SQLException {
        final ResultSetHandler<List<RegularScheduleEntry>> fh = new BeanListHandler<>(RegularScheduleEntry.class, Database.rowProcessor);
        final ColumnListHandler<String> th = new ColumnListHandler<>(1);
        List<List<RegularScheduleEntry>> result = new ArrayList<>();

        List<String> conflictIds = db.runner.query(query, th);
        for (String s: conflictIds) {
            result.add(this.getScheduleEntries(s.replaceAll("[\\[\\]]", "")));
        }

        return result;
    }

    private List<RegularScheduleEntry> getScheduleEntries(String ids) throws SQLException {
        final BeanListHandler<RegularScheduleEntry> h = new BeanListHandler<>(RegularScheduleEntry.class, Database.rowProcessor);
        return db.runner.query(String.format("select * from fcimapp.vw_denormalized_regular_schedule " +
                        "where schedule_entry_id in (%s)", ids), h);
    }
}
