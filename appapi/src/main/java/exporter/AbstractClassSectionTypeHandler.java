package exporter;

import org.apache.poi.ss.usermodel.Cell;
import pojos.GroupedRegularScheduleEntry;

public abstract class AbstractClassSectionTypeHandler {
    abstract public void onEmpty(Cell[] cells);

    abstract public void onError(Cell[] cells);

    abstract public void onNoParity(GroupedRegularScheduleEntry e, Cell[] cells);

    abstract public void onParityBoth(GroupedRegularScheduleEntry odd, GroupedRegularScheduleEntry even, Cell[] cells);

    abstract public void onParityOddOnly(GroupedRegularScheduleEntry odd, Cell[] cells);

    abstract public void onParityEvenOnly(GroupedRegularScheduleEntry even, Cell[] cells);
}
