package exporter.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class POIUtils {
    private static final Logger log = LoggerFactory.getLogger(POIUtils.class);
    private static final Pattern p = Pattern.compile(".*!\\$(\\w+)\\$(\\d+):\\$(\\w+)\\$(\\d+)");

    public static CellRangeAddress parsePrintArea(String printArea) {
        log.info("parsing print area: <{}>", printArea);

        Matcher m = p.matcher(printArea);
        if (m.matches()) {
            Integer firstCol = CellReference.convertColStringToIndex(m.group(1));
            Integer firstRow = Integer.parseInt(m.group(2)) - 1;
            Integer lastCol = CellReference.convertColStringToIndex(m.group(3));
            Integer lastRow = Integer.parseInt(m.group(4)) - 1;
            log.info("parsed print area: [firstCol: {}, firstRow: {}, lastCol: {}, lastRow: {}]", firstCol, firstRow, lastCol, lastRow);
            return new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        } else {
            throw new RuntimeException("Received print area in an unexpected format");
        }
    }

    public static void insertNewColumnBefore(Workbook wb, Sheet s, int pivotColumnIndex) {
        int maxNrOfCols = 0;
        int nrRows = s.getPhysicalNumberOfRows();

        // Adjust cell contents and styles
        for (int row = 0; row < nrRows; row++) {
            Row r = s.getRow(row);
            if (r == null) {
                continue;
            }
            int nrCols = r.getPhysicalNumberOfCells();
            maxNrOfCols = Math.max(nrCols, maxNrOfCols);
            // shift to right
            for (int col = nrCols; col > pivotColumnIndex; col--) {
                Cell rightCell = r.getCell(col);
                if (rightCell != null) {
                    r.removeCell(rightCell);
                }

                Cell leftCell = r.getCell(col - 1);
                if (leftCell != null) {
                    Cell newCell = r.createCell(col, leftCell.getCellTypeEnum());
                    cloneCell(newCell, leftCell);
                }
            }
        }

        // Adjust the column widths
        for (int col = maxNrOfCols; col > pivotColumnIndex; col--) {
            s.setColumnWidth(col, s.getColumnWidth(col - 1));
        }
        // Adjust merged cells
        List<CellRangeAddress> newRangeAddresses = new LinkedList<>();
        for (int i = s.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress range = s.getMergedRegion(i);
            if (range.getLastColumn() >= pivotColumnIndex) {
                newRangeAddresses.add(range);
                s.removeMergedRegion(i);
            }
        }
        newRangeAddresses.forEach(range -> {
            int newFirstCol = range.getFirstColumn();
            int newLastCol = range.getLastColumn() + 1;
            if (range.getFirstColumn() >= pivotColumnIndex) {
                newFirstCol++;
            }
            CellRangeAddress newCellRangeAddress = new CellRangeAddress(
                    range.getFirstRow(), range.getLastRow(),
                    newFirstCol, newLastCol);
            s.addMergedRegion(newCellRangeAddress);
            if (range.getFirstColumn() == pivotColumnIndex && range.getLastColumn() == pivotColumnIndex) {
                s.addMergedRegion(range);
            }
        });

        // Adjust print area
        CellRangeAddress ra = parsePrintArea(wb.getPrintArea(0));
        wb.setPrintArea(0, ra.getFirstColumn(), ra.getLastColumn() + 1, ra.getFirstRow(), ra.getLastRow());
    }
    /*
     * Takes an existing Cell and merges all the styles and formula into the new
     * one
     */
    private static void cloneCell(Cell cNew, Cell cOld) {
        cNew.setCellComment(cOld.getCellComment());
        cNew.setCellStyle(cOld.getCellStyle());

        switch (cOld.getCellTypeEnum()) {
            case BOOLEAN:
                cNew.setCellValue(cOld.getBooleanCellValue());
                break;
            case NUMERIC:
                cNew.setCellValue(cOld.getNumericCellValue());
                break;
            case STRING:
                cNew.setCellValue(cOld.getStringCellValue());
                break;
            case ERROR:
                cNew.setCellValue(cOld.getErrorCellValue());
                break;
            case FORMULA:
                cNew.setCellFormula(cOld.getCellFormula());
                break;
        }
    }
}
