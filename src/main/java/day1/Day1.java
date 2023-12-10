package day1;

import utils.DayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day1 {

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        DayUtils dayUtils = new DayUtils(1, 1);
        Stream<String> streamInput = dayUtils.getListInput().stream();
        dayUtils.startTimer();
        var i = streamInput
                .map(String::chars)
                .map(intStream -> intStream.filter(Character::isDigit))
                .map(e -> e.map(Character::getNumericValue))
                .map(IntStream::toArray)
                .filter(e -> e.length > 0)
                .map(array -> String.valueOf(array[0]) + array[array.length - 1])
                .map(Integer::parseInt)
                .reduce(Integer::sum)
                .orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(i);
    }

    private static void partTwo() {
        DayUtils dayUtils = new DayUtils(1, 2);
        Stream<String> streamInput = dayUtils.getListInput().stream();
        dayUtils.startTimer();
        var i = streamInput
                .map(Day1::getNumbers)
                .map(List::toArray)
                .map(array -> String.valueOf(array[0]) + array[array.length - 1])
                .map(Integer::parseInt)
                .reduce(Integer::sum)
                .orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(i);
    }


    private static List<Integer> getNumbers(String line) {
        List<Integer> numberList = new ArrayList<>();
        Map<Integer, String> occurrences = new TreeMap<>();
        int formIndex = 0;
        for (var entry : stringToInteger.entrySet()) {
            while (line.substring(formIndex).contains(entry.getKey())) {
                formIndex = line.indexOf(entry.getKey(), formIndex);
                occurrences.put(formIndex, entry.getKey());
                formIndex++;
            }
            formIndex = 0;
        }
        for (var occurrence : occurrences.entrySet()) {
            numberList.add(stringToInteger.get(occurrence.getValue()));
        }
        return numberList;
    }

    private static final Map<String, Integer> stringToInteger = new TreeMap<>();

    static {
        stringToInteger.putAll(Map.of(
                "one", 1,
                "two", 2,
                "three", 3,
                "four", 4,
                "five", 5,
                "six", 6,
                "seven", 7,
                "eight", 8,
                "nine", 9

        ));
        stringToInteger.putAll(Map.of(
                "1", 1,
                "2", 2,
                "3", 3,
                "4", 4,
                "5", 5,
                "6", 6,
                "7", 7,
                "8", 8,
                "9", 9
        ));
    }
}
