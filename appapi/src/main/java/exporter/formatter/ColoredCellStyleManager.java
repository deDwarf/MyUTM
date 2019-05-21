package exporter.formatter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

public class ColoredCellStyleManager extends CellStyleManager implements ICellStyleManager {
    public ColoredCellStyleManager(Workbook wb, HSSFColor color) {
        super(wb);
        CellStyle[] cs = new CellStyle[7];
        for (int i = 0; i < 7; i++) {
            cs[i] = wb.createCellStyle();
        }

        cs[0].cloneStyleFrom(middleCell);
        cs[1].cloneStyleFrom(topCell);
        cs[2].cloneStyleFrom(bottomCell);
        cs[3].cloneStyleFrom(middleCellDashedBottomBorder);
        cs[4].cloneStyleFrom(middleCellBoldDashedBottomBorder);
        cs[5].cloneStyleFrom(middleCellBold);
        cs[6].cloneStyleFrom(topCellBold);
        for (int i = 0; i < 7; i++) {
            cs[i].setFillForegroundColor(color.getIndex());
            cs[i].setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        middleCell = cs[0];
        topCell = cs[1];
        bottomCell = cs[2];
        middleCellDashedBottomBorder = cs[3];
        middleCellBoldDashedBottomBorder = cs[4];
        middleCellBold = cs[5];
        topCellBold = cs[6];
    }
}
