package exporter.formatter;

import exporter.ClassSectionTypeHandler;
import exporter.ClassSectionTypeResolver;
import exporter.ScheduleExporter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import pojos.GroupedRegularScheduleEntry;

import java.util.List;

public class ClassSectionStyleFormatter {
    private HSSFWorkbook wb;

    public ClassSectionStyleFormatter(HSSFWorkbook wb) {
        if (ScheduleExporter.TemplateConstant.ONE_CLASS_SECTION_CELL_HEIGHT.val != 6) {
            throw new IllegalStateException("Implemented to work exclusively with cell height = 6");
        }
        this.wb = wb;
    }

    public void format(List<GroupedRegularScheduleEntry> re, Cell[] cells) {
        assert cells != null;
        assert cells.length == 6;

        ClassSectionTypeResolver.resolve(re, new MyClassSectionTypeHandler(wb), cells);
    }

    private class MyClassSectionTypeHandler extends ClassSectionTypeHandler {
        private HSSFWorkbook wb;

        public MyClassSectionTypeHandler(HSSFWorkbook wb) {
            this.wb = wb;
        }

        @Override
        public void onEmpty(Cell[] cells) {

        }

        @Override
        public void onError(Cell[] cells) {

        }

        @Override
        public void onNoParity(GroupedRegularScheduleEntry e, Cell[] cells) {
            ICellStyleManager s = ColoredCellStyleManager.getInstance(wb, e.getSubjectTypeId());
            cells[0].setCellStyle(s.getTopCell());
            cells[1].setCellStyle(s.getMiddleCellBold());
            cells[2].setCellStyle(s.getMiddleCellBold());
            cells[3].setCellStyle(s.getMiddleCell());
            cells[4].setCellStyle(s.getMiddleCell());
            cells[5].setCellStyle(s.getBottomCell());
        }

        @Override
        public void onParityBoth(GroupedRegularScheduleEntry odd, GroupedRegularScheduleEntry even, Cell[] cells) {
            ICellStyleManager odds = ColoredCellStyleManager.getInstance(wb, odd.getSubjectTypeId());
            ICellStyleManager evens = ColoredCellStyleManager.getInstance(wb, even.getSubjectTypeId());
            styleParity(odds, evens, cells);
        }

        @Override
        public void onParityEvenOnly(GroupedRegularScheduleEntry even, Cell[] cells) {
            ICellStyleManager evens = ColoredCellStyleManager.getInstance(wb, even.getSubjectTypeId());
            ICellStyleManager emptys = ColoredCellStyleManager.getInstance(wb, (long) -1);
            styleParity(emptys, evens, cells);

        }

        @Override
        public void onParityOddOnly(GroupedRegularScheduleEntry odd, Cell[] cells) {
            ICellStyleManager odds = ColoredCellStyleManager.getInstance(wb, odd.getSubjectTypeId());
            ICellStyleManager emptys = ColoredCellStyleManager.getInstance(wb, (long) -1);
            styleParity(odds, emptys, cells);
        }

        private void styleParity(ICellStyleManager oddStyle, ICellStyleManager evenStyle, Cell[] cells) {
            cells[0].setCellStyle(oddStyle.getTopCellBold());
            cells[1].setCellStyle(oddStyle.getMiddleCell());
            cells[2].setCellStyle(oddStyle.getMiddleCellDashedBottomBorder());
            cells[3].setCellStyle(evenStyle.getMiddleCellBold());
            cells[4].setCellStyle(evenStyle.getMiddleCell());
            cells[5].setCellStyle(evenStyle.getBottomCell());
        }
    }
}
