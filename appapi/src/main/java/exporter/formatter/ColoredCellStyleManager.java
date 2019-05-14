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
    private static Map<String, ICellStyleManager> inst;
    private static boolean initialized = false;

    private ColoredCellStyleManager(Workbook wb, HSSFColor color) {
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

    public static ICellStyleManager getInstance(HSSFWorkbook wb, Long subjectTypeId) {
       if (!initialized) {
           HSSFColor yellow = wb.getCustomPalette().findSimilarColor((byte)253 , (byte)233, (byte)217);
           HSSFColor grey = wb.getCustomPalette().getColor(IndexedColors.GREY_25_PERCENT.getIndex());
           inst = new HashMap<>();
           inst.put("yellow", new ColoredCellStyleManager(wb, yellow));
           inst.put("grey", new ColoredCellStyleManager(wb, grey));
           inst.put("default", new CellStyleManager(wb));
           initialized = true;
       }

       if (subjectTypeId == 3) {
           return inst.get("yellow");
       } else if (subjectTypeId == 2 || subjectTypeId == 1) {
           return inst.get("grey");
       } else {
           return inst.get("default");
       }
    }
}
