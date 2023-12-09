package day8;

import utils.DayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 {
    // example of node: NFK = (LMH, RSS)
    private static final Pattern NODE_PATTERN = Pattern.compile("(\\w+) = \\((\\w+), (\\w+)\\)");

    public static void main(String[] args) {
        caseOne();
        caseTwo();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(8, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        Map<String, List<String>> nodes = getNodes(input);
        String currentNode = "AAA";
        long steps = getSteps(currentNode, nodes, input, e -> e.equals("ZZZ"));
        dayUtils.endTimer();
        dayUtils.printAnswer(steps);
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(8, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        Map<String, List<String>> nodes = getNodes(input);
        List<String> aNodes = nodes.keySet().stream() // get nodes that end with A (XXA)
                .filter(id -> id.charAt(2) == 'A').toList();
        List<Integer> steps = aNodes.stream()
                .map(aNode -> getSteps(aNode, nodes, input, id -> id.charAt(2) == 'Z')).toList();
        Long lCM = steps.stream().map(e -> (long) e).reduce(Day8::lCM).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(lCM);
    }

    private static Map<String, List<String>> getNodes(List<String> input) {
        Map<String, List<String>> nodes = new TreeMap<>();
        input.stream().skip(2)
                .map(line -> NODE_PATTERN.matcher(line).results().map(MatchResult::group).toList())
                .forEach(e -> nodes.put(e.get(1), List.of(e.get(2), e.get(3))));
        return nodes;
    }

    private static int getSteps(String currentNode, Map<String, List<String>> nodes, List<String> input, Predicate<String> condition) {
        int[] instructions = input.getFirst().chars().map(key -> key == 'L' ? 0 : 1).toArray();
        int steps = 0;
        while (true) {
            for (int instruction : instructions) {
                steps++;
                currentNode = nodes.get(currentNode).get(instruction);
                if (condition.test(currentNode)) {
                    return steps;
                }
            }
        }
    }

    // lowest common multiplier
    private static long lCM(long a, long b) {
        return (a * b) / gCD(a, b);
    }

    // greatest common divisor // euclidean algorithm
    private static long gCD(long n1, long n2) {
        if (n2 == 0) {
            return n1;
        }
        return gCD(n2, n1 % n2);
    }
}
