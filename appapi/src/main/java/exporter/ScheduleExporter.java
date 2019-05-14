package exporter;
import core.AppContext;
import core.Database;
import exporter.formatter.ClassSectionStyleFormatter;
import exporter.formatter.ClassSectionTextFormatter;
import exporter.utils.POIUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojos.Group;
import pojos.RegularScheduleEntry;

public class ScheduleExporter implements IScheduleExporter {
    private static final String studentScheduleTemplatePath = "regular_schedule_template.xls";
    private static final String teacherScheduleTemplatePath = "regular_schedule_template_teachers.xls";
    private static final Logger log = LoggerFactory.getLogger(ScheduleExporter.class);
    private static final Database db = AppContext.getInstance().getDB();

    @Override
    public File exportStudentSchedule(final List<Long> ids) throws IOException, SQLException {
        List<Group> groups = db.getGroups();
        this.validateGroups(ids, groups);

        File f = File.createTempFile("filled-schedule-template-", ".xls");
        FileOutputStream fout = new FileOutputStream(f);
        java.net.URL url = IOUtils.resourceToURL(studentScheduleTemplatePath, ScheduleExporter.class.getClassLoader());
        try {
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(new File(url.toURI())));
            Sheet sheet = wb.getSheet("Schedule");
            ClassSectionStyleFormatter styleFormatter = new ClassSectionStyleFormatter(wb);
            ClassSectionTextFormatter textFormatter = new ClassSectionTextFormatter();
            List<RegularScheduleEntry> schedule = new ArrayList<>();
            for(long i: ids) {
                schedule.addAll(db.getRegularSchedule(i));
            }

            this.initGroupColumnDefs(ids, groups, wb, sheet);
            this.populatePayload(schedule, ids, sheet, styleFormatter, textFormatter);

            wb.write(fout);
            fout.flush();
            fout.close();
            wb.close();
            return f;
        } catch (URISyntaxException e) {
            throw new IOException("Error while schedule export: cannot read from template file");
        }
    }

    @Override
    public File exportTeacherSchedule(List<Long> ids) {
        return null;
    }

    private Map<Long, Integer> initGroupColumnDefs(List<Long> ids, List<Group> groups, Workbook wb, Sheet sh) {
        Map<Long, Integer> groupToColIndexMapping = new HashMap<>();
        groupToColIndexMapping.put(ids.get(0), TemplateConstant.GROUP_LIST_START_COL.val);
        for (int i = 1; i < ids.size(); i++) {
            POIUtils.insertNewColumnBefore(wb, sh, TemplateConstant.GROUP_LIST_START_COL.val);
            groupToColIndexMapping.put(ids.get(i), (TemplateConstant.GROUP_LIST_START_COL.val + i));
        }
        log.info("initialized column placeholders for groups. Id to index mapping: {}", groupToColIndexMapping);

        for (Group group : groups) {
            Integer index = groupToColIndexMapping.get(group.getGroupId());
            if (index != null) {
                sh.getRow(TemplateConstant.GROUP_LIST_DATA_ROW.val).getCell(index).setCellValue(group.getGroupName());
            }
        }

        return groupToColIndexMapping;
    }

    private void populatePayload(List<RegularScheduleEntry> sch, List<Long> groupIds, Sheet sh,
                                 ClassSectionStyleFormatter sFormatter, ClassSectionTextFormatter tFormatter) {
        // very very deep loop
        for (int day = 0; day < TemplateConstant.NUM_OF_DAYS.val; day++) {
            final int closureDay = day;
            for (int classNumber = 1; classNumber <= TemplateConstant.NUM_OF_CLASSES.val; classNumber++) {
                log.info("Processing day <{}>, class number <{}>", day, classNumber);
                final int closureClassNumber = classNumber;
                List<RegularScheduleEntry> schForTime = sch.stream()
                        .filter(e -> e.getWeekDay() == closureDay && e.getClassNumber() == closureClassNumber)
                        .collect(Collectors.toList());

                int i = 0;
                while (i < groupIds.size())  {
                    final int closureI = i;
                    List<RegularScheduleEntry> schForTimeAndGroup = schForTime.stream()
                            .filter(e -> e.getGroupId() == groupIds.get(closureI))
                            .collect(Collectors.toList());
                    ClassSectionType classSectionType = ClassSectionType.getCellType(schForTimeAndGroup);
                    // lookahead to determine cells to be merged
                    int j;
                    for (j = i + 1; j < groupIds.size(); j++) {
                        final int closureJ = j;
                        List<RegularScheduleEntry> schForTimeAndGroup1 = schForTime.stream()
                                .filter(e -> e.getGroupId() == groupIds.get(closureJ) && e.getWeekParity() == null)
                                .collect(Collectors.toList());
                        ClassSectionType nextClassSectionType = ClassSectionType.getCellType(schForTimeAndGroup1);
                        if (classSectionType == ClassSectionType.EMPTY || nextClassSectionType == ClassSectionType.EMPTY
                                || nextClassSectionType != classSectionType
                                || !isSameDayTeacherAndSubject(schForTimeAndGroup.get(0), schForTimeAndGroup1.get(0))) {
                            break;
                        }
                    }

                    // at least one cell to be merged
                    CellAddress base = TemplateConstant.getStartCellAddr(day, classNumber);
                    if (j > i + 1) {
                        log.info("Merging cells from [{} to {})", i, j);
                        for (int k = 0; k < TemplateConstant.ONE_CLASS_SECTION_CELL_HEIGHT.val; k++) {
                            CellRangeAddress mergeRegion = new CellRangeAddress(base.getRow() + k,
                                    base.getRow() + k,
                                    base.getColumn() + i,
                                    base.getColumn() + j - 1);
                            sh.addMergedRegion(mergeRegion);
                        }
                    }

                    CellRangeAddress dataCol = new CellRangeAddress(base.getRow(),
                            base.getRow() + TemplateConstant.ONE_CLASS_SECTION_CELL_HEIGHT.val - 1,
                            base.getColumn() + i,
                            base.getColumn() + i);
                    Cell[] cells = IteratorUtils.toList(dataCol.iterator(), 6)
                            .stream()
                            .map(e -> sh.getRow(e.getRow()).getCell(e.getColumn()))
                            .toArray(Cell[]::new);

                    tFormatter.format(schForTimeAndGroup, cells, TemplateConstant.CELL_LENGTH.val * (j - i));
                    sFormatter.format(schForTimeAndGroup, cells);
                    // continue starting for j - if there was a merge, don`t jump over merged cells
                    i = j;
                }
            }
        }
    }

    private void validateGroups(final List<Long> ids, List<Group> groups) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("Cannot generate schedule when no groups is provided");
        }
        for (Long id: ids) {
            boolean exists = false;
            for(Group g: groups) {
                if (g.getGroupId() == id) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                throw new RuntimeException("Some of the given group ids are not valid");
            }
        }
    }

    private boolean isSameDayTeacherAndSubject(RegularScheduleEntry e, RegularScheduleEntry e2) {
        return e.getSubjectId() == e2.getSubjectId()
                && e.getTeacherId() == e2.getTeacherId()
                && e.getClassroomId() == e2.getClassroomId()
                && e.getClassNumber() == e2.getClassNumber()
                && e.getWeekDay() == e2.getWeekDay()
                && Objects.equals(e.getWeekParity(), e2.getWeekParity());
    }

    public enum TemplateConstant {
        NUM_OF_DAYS(5),
        NUM_OF_CLASSES(7),
        CELL_LENGTH(25),
        ONE_CLASS_SECTION_CELL_HEIGHT(6),
        WEEKDAY_DELIMITER_SIZE(1),
        GROUP_LIST_START_COL(4),
        GROUP_LIST_DATA_ROW(7),
        PAYLOAD_START_ROW(10),
        PAYLOAD_START_COL(4);

        public final int val;

        TemplateConstant(int value) {
            this.val = value;
        }

        /**
         * Return cells address to start writing from
         *
         * @param day Monday is zero (0-based)
         * @param classNumber 1st class is '1' (1-based)
         * @return basically, the row in the spreadsheet corresponding to given day and class.
         *         Column number will stand for PAYLOAD_START_COL
         */
        public static CellAddress getStartCellAddr(int day, int classNumber) {
            int dayHeight = (NUM_OF_CLASSES.val * ONE_CLASS_SECTION_CELL_HEIGHT.val + WEEKDAY_DELIMITER_SIZE.val);
            int rowNum = PAYLOAD_START_ROW.val + dayHeight * day + (classNumber - 1) * ONE_CLASS_SECTION_CELL_HEIGHT.val;
            return new CellAddress(rowNum, PAYLOAD_START_COL.val);
        }
    }

}
