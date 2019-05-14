package exporter.formatter;

import org.apache.poi.ss.usermodel.CellStyle;

public interface ICellStyleManager {
    CellStyle getTopCellBold();

    CellStyle getTopCell();

    CellStyle getMiddleCellBoldDashedBottomBorder();

    CellStyle getMiddleCellDashedBottomBorder();

    CellStyle getMiddleCellBold();

    CellStyle getMiddleCell();

    CellStyle getBottomCell();
}
