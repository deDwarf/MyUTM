package exporter.formatter;

import com.sun.xml.internal.ws.util.StreamUtils;
import exporter.ClassSectionTypeHandler;
import exporter.ClassSectionTypeResolver;
import exporter.ScheduleExporter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import pojos.GroupedRegularScheduleEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        private ICellStyleManager syellow;
        private ICellStyleManager sgrey;
        private ICellStyleManager sdefault;

        public MyClassSectionTypeHandler(HSSFWorkbook wb) {
            this.wb = wb;
            HSSFColor yellow = wb.getCustomPalette().findSimilarColor((byte)253 , (byte)233, (byte)217);
            HSSFColor grey = wb.getCustomPalette().getColor(IndexedColors.GREY_25_PERCENT.getIndex());
            syellow = new ColoredCellStyleManager(wb, yellow);
            sgrey = new ColoredCellStyleManager(wb, grey);
            sdefault = new CellStyleManager(wb);
        }

        private ICellStyleManager getStyleBySubjectType(long subjectTypeId) {
            if (subjectTypeId == 3) {
                return syellow;
            } else if (subjectTypeId == 2 || subjectTypeId == 1) {
                return sgrey;
            } else {
                return sdefault;
            }
        }

        @Override
        public void onEmpty(Cell[] cells) {

        }

        @Override
        public void onError(Cell[] cells) {

        }

        @Override
        public void onNoParity(GroupedRegularScheduleEntry e, Cell[] cells) {
            ICellStyleManager s = this.getStyleBySubjectType(e.getSubjectTypeId());
            cells[0].setCellStyle(s.getTopCell());
            cells[1].setCellStyle(s.getMiddleCellBold());
            cells[2].setCellStyle(s.getMiddleCellBold());
            cells[3].setCellStyle(s.getMiddleCell());
            cells[4].setCellStyle(s.getMiddleCell());
            cells[5].setCellStyle(s.getBottomCell());
        }

        @Override
        public void onParityBoth(GroupedRegularScheduleEntry odd, GroupedRegularScheduleEntry even, Cell[] cells) {
            ICellStyleManager odds = this.getStyleBySubjectType(odd.getSubjectTypeId());
            ICellStyleManager evens = this.getStyleBySubjectType(even.getSubjectTypeId());
            styleParity(odds, evens, cells);
        }

        @Override
        public void onParityEvenOnly(GroupedRegularScheduleEntry even, Cell[] cells) {
            ICellStyleManager evens = this.getStyleBySubjectType(even.getSubjectTypeId());
            ICellStyleManager emptys = this.getStyleBySubjectType((long) -1);
            styleParity(emptys, evens, cells);

        }

        @Override
        public void onParityOddOnly(GroupedRegularScheduleEntry odd, Cell[] cells) {
            ICellStyleManager odds = this.getStyleBySubjectType(odd.getSubjectTypeId());
            ICellStyleManager emptys = this.getStyleBySubjectType((long) -1);
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
