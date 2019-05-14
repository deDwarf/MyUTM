package exporter.formatter;

import org.apache.poi.ss.usermodel.*;

public class CellStyleManager implements ICellStyleManager {
    CellStyle middleCell;
    CellStyle topCell;
    CellStyle bottomCell;
    CellStyle middleCellDashedBottomBorder;
    CellStyle middleCellBoldDashedBottomBorder;
    CellStyle middleCellBold;
    CellStyle topCellBold;

    public CellStyleManager(Workbook wb) {
        Font commonFont = wb.createFont();
        Font commonBoldFont = wb.createFont();
        commonBoldFont.setBold(true);

        middleCell = wb.createCellStyle();
        middleCell.setBorderLeft(BorderStyle.MEDIUM);
        middleCell.setBorderRight(BorderStyle.MEDIUM);
        middleCell.setFont(commonFont);
        middleCell.setAlignment(HorizontalAlignment.CENTER);

        topCell = wb.createCellStyle();
        topCell.cloneStyleFrom(middleCell);
        topCell.setBorderTop(BorderStyle.MEDIUM);

        topCellBold = wb.createCellStyle();
        topCellBold.cloneStyleFrom(topCell);
        topCellBold.setFont(commonBoldFont);

        bottomCell = wb.createCellStyle();
        bottomCell.cloneStyleFrom(middleCell);
        bottomCell.setBorderBottom(BorderStyle.MEDIUM);

        middleCellDashedBottomBorder = wb.createCellStyle();
        middleCellDashedBottomBorder.cloneStyleFrom(middleCell);
        middleCellDashedBottomBorder.setBorderBottom(BorderStyle.MEDIUM_DASHED);

        middleCellBoldDashedBottomBorder = wb.createCellStyle();
        middleCellBoldDashedBottomBorder.cloneStyleFrom(middleCellDashedBottomBorder);
        middleCellBoldDashedBottomBorder.setFont(commonBoldFont);

        middleCellBold = wb.createCellStyle();
        middleCellBold.cloneStyleFrom(middleCell);
        middleCellBold.setFont(commonBoldFont);
    }

    public CellStyle getTopCellBold() {
        return topCellBold;
    }

    public CellStyle getTopCell() {
        return topCell;
    }

    public CellStyle getMiddleCellBoldDashedBottomBorder() {
        return middleCellBoldDashedBottomBorder;
    }

    public CellStyle getMiddleCellDashedBottomBorder() {
        return middleCellDashedBottomBorder;
    }

    public CellStyle getMiddleCellBold() {
        return middleCellBold;
    }

    public CellStyle getMiddleCell() {
        return middleCell;
    }

    public CellStyle getBottomCell() {
        return bottomCell;
    }
}
