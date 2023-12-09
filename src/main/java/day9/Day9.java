package day9;

import utils.DayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Day9 {

    private static final Pattern NUMBER_WITH_NEGATIVE = Pattern.compile("-?\\d+");

    public static void main(String[] args) {
        caseOne();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(9, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        Long sum = input.stream()
                .map(Day9::getSequence)
                .peek(System.out::println)
                .map(Day9::getNextNumber)
                .peek(System.out::println)
                .reduce(Long::sum).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static List<Long> getSequence(String input) {
        return NUMBER_WITH_NEGATIVE.matcher(input).results()
                .map(MatchResult::group).map(Long::parseLong)
                .toList();
    }

    private static long getNextNumber(List<Long> sequence) {
        List<Long> differences = getDifferences(sequence);
        if (differences.stream().anyMatch(x -> x != 0)) {
            return sequence.getLast() + getNextNumber(differences);
        } else {
            return sequence.getLast();
        }
    }

    private static List<Long> getDifferences(List<Long> numbers) {
        List<Long> differences = new ArrayList<>();
        for (int i = 1; i < numbers.size(); i++) {
            differences.add(numbers.get(i) - numbers.get(i - 1));
        }
        return differences;
    }
}

