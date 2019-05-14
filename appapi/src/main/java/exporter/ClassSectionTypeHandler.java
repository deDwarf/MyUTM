package exporter;

import org.apache.poi.ss.usermodel.Cell;
import pojos.RegularScheduleEntry;

public abstract class ClassSectionTypeHandler {
    abstract public void onEmpty(Cell[] cells);

    abstract public void onError(Cell[] cells);

    abstract public void onNoParity(RegularScheduleEntry e, Cell[] cells);

    abstract public void onParityBoth(RegularScheduleEntry odd, RegularScheduleEntry even, Cell[] cells);

    abstract public void onParityOddOnly(RegularScheduleEntry odd, Cell[] cells);

    abstract public void onParityEvenOnly(RegularScheduleEntry even, Cell[] cells);
}
