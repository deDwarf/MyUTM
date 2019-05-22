package exporter.formatter;


import exporter.AbstractClassSectionTypeHandler;
import exporter.ClassSectionTypeResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import pojos.GroupedRegularScheduleEntry;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassSectionTextFormatter {
    private static Map<Integer, AbstractClassSectionTypeHandler> hs;
    private Function<GroupedRegularScheduleEntry, String> f;

    public ClassSectionTextFormatter(Function<GroupedRegularScheduleEntry, String> thirdCellContentExtractor) {
        hs = new HashMap<>();
        this.f = thirdCellContentExtractor;
    }

    public void format(List<GroupedRegularScheduleEntry> sch, Cell[] cells, int maxLength) {
        assert cells != null;
        assert cells.length == 6;

        AbstractClassSectionTypeHandler h = hs.get(maxLength);
        if (Objects.isNull(h)) {
            hs.put(maxLength, new TextClassSectionTypeHandler(maxLength));
            h = hs.get(maxLength);
        }
        ClassSectionTypeResolver.resolve(sch, h, cells);
    }

    public static Pair<String, String> formatTwoStringSubjectName(
            String subjName, String subjType, String subjNameAbbr, int fieldMaxLen) {

        String subjDisplayName = subjType.concat(" ").concat(subjName);
        MutablePair<String, String> res = new MutablePair<>();
        if (subjDisplayName.length() < fieldMaxLen) {
            res.setLeft(subjDisplayName);
            res.setRight("");
            return res;
        }
        if (subjDisplayName.length() >= 2 * fieldMaxLen) {
            res.setLeft(subjType.concat(". ").concat(subjNameAbbr));
            res.setRight("(".concat(StringUtils.abbreviate(subjName, fieldMaxLen - 2)).concat(")"));
            return res;
        }

        List<String> chunks = Arrays.asList(subjDisplayName.split(" "));
        // + 1 to keep spaces
        List<Integer> chunkLengths = chunks.stream().mapToInt(value -> value.length() + 1).boxed().collect(Collectors.toList());
        chunkLengths.set(chunkLengths.size() - 1, chunkLengths.get(chunkLengths.size() - 1) - 1);
        int totalChunks = chunks.size();

        List<Pair<Integer, Integer>> validSplits = new ArrayList<>();
        for (int i = totalChunks - 1; i >= 0; i--) {
            int a = chunkLengths.subList(0, i).stream().reduce((i1, i2) -> i1 + i2).orElse(-1);
            int b = chunkLengths.subList(i, totalChunks).stream().reduce((i1, i2) -> i1 + i2).orElse(-1);
            if (a < fieldMaxLen && b < fieldMaxLen) {
                validSplits.add(new ImmutablePair<>(i, Math.abs(a - b)));
            }
        }
        // {chunk | a - b = min(a - b) of chunks}
        Optional<Pair<Integer, Integer>> bestSplit = validSplits.stream().min(Comparator.comparingInt(Pair::getRight));
        if (bestSplit.isPresent()) {
            int chunkDelimiterIndex = bestSplit.get().getLeft();
            res.setLeft(String.join(" ", chunks.subList(0, chunkDelimiterIndex)));
            res.setRight(String.join(" ", chunks.subList(chunkDelimiterIndex, totalChunks)));
        } else {
            res.setLeft(subjType.concat(". ").concat(subjNameAbbr));
            res.setRight("(".concat(StringUtils.abbreviate(subjName, fieldMaxLen - 2)).concat(")"));
            return res;
        }

        return res;
    }

    private class TextClassSectionTypeHandler extends AbstractClassSectionTypeHandler {
        private int maxCellLength;

        public TextClassSectionTypeHandler(int maxCellLength) {
            this.maxCellLength = maxCellLength;
        }

        @Override
        public void onEmpty(Cell[] cells) {
        }

        @Override
        public void onError(Cell[] cells) {
            cells[0].setCellValue("Error");
        }

        @Override
        public void onNoParity(GroupedRegularScheduleEntry e, Cell[] cells) {
            Pair<String, String> subjname = formatTwoStringSubjectName(e.getSubjectName()
                    , e.getSubjectTypeAbbreviated(), e.getSubjectNameAbbreviated(), maxCellLength);
            cells[1].setCellValue(subjname.getLeft());
            cells[2].setCellValue(subjname.getRight());
            cells[3].setCellValue(e.getClassroomName());
            cells[4].setCellValue(f.apply(e));
        }

        @Override
        public void onParityBoth(GroupedRegularScheduleEntry odd, GroupedRegularScheduleEntry even, Cell[] cells) {
            onParityEvenOnly(even, cells);
            onParityOddOnly(odd, cells);
        }

        @Override
        public void onParityOddOnly(GroupedRegularScheduleEntry odd, Cell[] cells) {
            cells[0].setCellValue(odd.getSubjectTypeAbbreviated().concat(" ").concat(odd.getSubjectNameAbbreviated()));
            cells[1].setCellValue(odd.getClassroomName());
            cells[2].setCellValue(f.apply(odd));
        }

        @Override
        public void onParityEvenOnly(GroupedRegularScheduleEntry even, Cell[] cells) {
            cells[3].setCellValue(even.getSubjectTypeAbbreviated().concat(" ").concat(even.getSubjectNameAbbreviated()));
            cells[4].setCellValue(even.getClassroomName());
            cells[5].setCellValue(f.apply(even));
        }
    }
}
