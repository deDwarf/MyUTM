package exporter;

import pojos.GroupedRegularScheduleEntry;
import java.util.List;

public enum ClassSectionType {
    NO_PARITY(false, false),
    PARITY_ODD_ONLY(true, false),
    PARITY_EVEN_ONLY(true, false),
    PARITY_BOTH(true, false),

    NO_PARITY_SUBGROUPS(false, true),
    PARITY_ODD_ONLY_SUBGROUPS(true, true),
    PARITY_EVEN_ONLY_SUBGROUPS(true, true),
    PARITY_BOTH_SUBGROUPS(true, true),

    EMPTY(false, false),
    ERROR(false, false);

    private boolean parity;
    private boolean subgroups;

    ClassSectionType(boolean parity, boolean subgroups) {
        this.parity = parity;
        this.subgroups = subgroups;
    }

    public static ClassSectionType getCellType(List<GroupedRegularScheduleEntry> es) {
        return ClassSectionTypeResolver.resolve(es);
    }

    public boolean isParity() {
        return parity;
    }

    public boolean isSubgroups() {
        return subgroups;
    }
}
