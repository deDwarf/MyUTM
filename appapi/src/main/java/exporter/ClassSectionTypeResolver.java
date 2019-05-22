package exporter;

import org.apache.poi.ss.usermodel.Cell;
import pojos.GroupedRegularScheduleEntry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClassSectionTypeResolver {
    private static StubHandler STUB_HANDLER = new StubHandler();

    public static ClassSectionType resolve(List<GroupedRegularScheduleEntry> sch) {
        return resolve(sch, STUB_HANDLER, null);
    }

    public static ClassSectionType resolve(List<GroupedRegularScheduleEntry> sch, AbstractClassSectionTypeHandler h, Cell[] cells) {
        if (sch == null || sch.isEmpty()) {
            h.onEmpty(cells);
            return ClassSectionType.EMPTY;
        }

        if (sch.size() == 1 && Objects.isNull(sch.get(0).getSubgroup())) {
            if (Objects.isNull(sch.get(0).getWeekParity())) {
                h.onNoParity(sch.get(0), cells);
                return ClassSectionType.NO_PARITY;
            }
            else if ("odd".equalsIgnoreCase(sch.get(0).getWeekParity())) {
                h.onParityOddOnly(sch.get(0), cells);
                return ClassSectionType.PARITY_ODD_ONLY;
            }
            else if ("even".equalsIgnoreCase(sch.get(0).getWeekParity())) {
                h.onParityEvenOnly(sch.get(0), cells);
                return ClassSectionType.PARITY_EVEN_ONLY;
            } else {
                h.onError(cells);
                return ClassSectionType.ERROR;
            }
        }
        if (sch.size() == 2) {
            Optional<GroupedRegularScheduleEntry> odd = sch.stream()
                    .filter(e -> "odd".equals(e.getWeekParity()))
                    .findFirst();
            Optional<GroupedRegularScheduleEntry> even = sch.stream()
                    .filter(e -> "even".equals(e.getWeekParity()))
                    .findFirst();
            if (odd.isPresent() && odd.get().getSubgroup() == null
                    && even.isPresent() && even.get().getSubgroup() == null ) {
                h.onParityBoth(odd.get(), even.get(), cells);
                return ClassSectionType.PARITY_BOTH;
            } else {
                return ClassSectionType.ERROR;
            }
        }
        h.onError(cells);
        return ClassSectionType.ERROR;
        // TODO subgroups
    }

    private static class StubHandler extends AbstractClassSectionTypeHandler {

        @Override
        public void onEmpty(Cell[] cells) { }

        @Override
        public void onError(Cell[] cells) { }

        @Override
        public void onNoParity(GroupedRegularScheduleEntry e, Cell[] cells) { }

        @Override
        public void onParityBoth(GroupedRegularScheduleEntry odd, GroupedRegularScheduleEntry even, Cell[] cells) { }

        @Override
        public void onParityOddOnly(GroupedRegularScheduleEntry odd, Cell[] cells) { }

        @Override
        public void onParityEvenOnly(GroupedRegularScheduleEntry even, Cell[] cells) { }
    }
}
