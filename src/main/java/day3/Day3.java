package day3;

import utils.DayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.RegexPatterns.NUMBER;

public class Day3 {
    private static final Pattern GEAR = Pattern.compile("[*]");
    private static final String INVALID = "[\\d.]+";

    public static final int ROW_LENGTH = 140;

    public static void main(String[] args) {
        caseOne();
        caseTwo();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(3, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        adjustInput(input);
        int sum = 0;
        for (int i = 1; i < input.size() - 1; i++) {
            String currentRow = input.get(i);
            Matcher matcher = NUMBER.matcher(currentRow);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                int adjustedStart = start == 0 ? start : start - 1;
                int adjustedEnd = end == ROW_LENGTH ? end : end + 1;

                String cutTopRow = input.get(i - 1).substring(adjustedStart, adjustedEnd);
                String cutCurrentRow = input.get(i).substring(adjustedStart, adjustedEnd);
                String cutBottomRow = input.get(i + 1).substring(adjustedStart, adjustedEnd);
                if (!cutTopRow.matches(INVALID) ||
                        !cutBottomRow.matches(INVALID) ||
                        !cutCurrentRow.matches(INVALID)
                ) {
                    sum += Integer.parseInt(matcher.group());
                }
            }
        }
        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(3, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        adjustInput(input);
        int sum = 0;
        for (int i = 1; i < input.size() - 1; i++) {
            String currentRow = input.get(i);
            Matcher matcher = GEAR.matcher(currentRow);
            while (matcher.find()) {
                int gearIndex = matcher.start();
                List<NumberPosition> positions = getNumberPositions(input, i, gearIndex);
                if (positions.size() == 2) {
                    int gearRatio = extractGearRatio(input, positions, i);
                    sum += gearRatio;
                }
            }
        }
        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static int extractGearRatio(List<String> input, List<NumberPosition> positions, int i) {
        int gearRatio = 1;
        for (NumberPosition position : positions) {
            String row = input.get(i + position.row);
            int startIndex = position.index;
            while (startIndex >= 0 && Character.isDigit(row.charAt(startIndex))) {
                startIndex--;
            }
            startIndex++;
            int endIndex = position.index;
            while (endIndex < ROW_LENGTH && Character.isDigit(row.charAt(endIndex))) {
                endIndex++;
            }
            gearRatio *= Integer.parseInt(row.substring(startIndex, endIndex));
        }
        return gearRatio;
    }

    private static List<NumberPosition> getNumberPositions(List<String> input, int i, int center) {
        List<NumberPosition> positions = new ArrayList<>();
        int left = center == 0 ? center : center - 1;
        int right = center == ROW_LENGTH ? center : center + 1;
        ROW_LOOP:
        for (int row = -1; row <= 1; row++) { // top current bottom
            boolean wasPreviousNumber = false;
            COLUMN_LOOP:
            for (int index = left; index <= right; index++) { // left center right
                char c = input.get(i + row).charAt(index);
                boolean isNumber = Character.isDigit(c);
                if (isNumber && !wasPreviousNumber) { // registers new numbers
                    positions.add(new NumberPosition(row, index));
                    wasPreviousNumber = true;
                    if (positions.size() > 2) { // 6 in one possible, break prematurely
                        break ROW_LOOP;
                    }
                } else if (!isNumber && wasPreviousNumber) { // ends number
                    wasPreviousNumber = false;
                }
            }
        }
        return positions;
    }

    private static void adjustInput(List<String> input) {
        input.addFirst(".".repeat(ROW_LENGTH));
        input.addLast(".".repeat(ROW_LENGTH));
    }

    public record NumberPosition(int row, int index) {

    }
}
