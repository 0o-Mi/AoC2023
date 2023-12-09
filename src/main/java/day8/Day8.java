package day8;

import utils.DayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 {
    // example of node: NFK = (LMH, RSS)
    private static final Pattern NODE_PATTERN = Pattern.compile("(\\w+) = \\((\\w+), (\\w+)\\)");

    private static final Map<Character, Integer> INSTRUCTION_MAP = Map.of(
            'L', 0,
            'R', 1
    );

    public static void main(String[] args) {
        caseOne();
        caseTwo();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(8, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        int[] instructions = input.getFirst().chars().map(key -> INSTRUCTION_MAP.get((char) key)).toArray();
//        System.out.println(Arrays.toString(instructions));
        Map<String, List<String>> nodes = getNodes(input);
        String currentNode = "AAA";
        int steps = getSteps(currentNode, instructions, nodes);
//        nodes.forEach((k,v) -> System.out.println(k + " " + v));
        dayUtils.endTimer();
        dayUtils.printAnswer(steps);
    }

    private static int getSteps(String currentNode, int[] instructions, Map<String, List<String>> nodes) {
        int steps = 0;
        while (!currentNode.equals("ZZZ")) {
            for (int instruction : instructions) {
                steps++;
                currentNode = nodes.get(currentNode).get(instruction);
                if (currentNode.equals("ZZZ")) {
                    break;
                }
            }
        }
        return steps;
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(8, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        int[] instructions = input.getFirst().chars().map(key -> INSTRUCTION_MAP.get((char) key)).toArray();
//        System.out.println(Arrays.toString(instructions));
        Map<String, List<String>> nodes = getNodes(input);
//        System.out.println("nodes " + nodes);
        List<String> endWithANodes = nodes.keySet().stream() // get nodes that end with A (XXA)
                .filter(id -> endsWithChar(id, 'A'))
                .collect(Collectors.toCollection(ArrayList::new));
//        System.out.println("currentNodes: " + currentNodes);
//        int steps = getStepsBruteForce(currentNodes, instructions, nodes);
        List<Integer> stepsList = new ArrayList<>();
//        System.out.println(endWithANodes);
        for (String endWithANode : endWithANodes) {
            int steps = getStepsCaseTwo(endWithANode, instructions, nodes);
            stepsList.add(steps);
        }
//        System.out.println(stepsList);
        long previous = stepsList.get(0);
        for (int i = 1; i < stepsList.size(); i++) {
            previous = lCM(previous, stepsList.get(i));
        }
        dayUtils.endTimer();
        dayUtils.printAnswer(previous);
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

    private static int getStepsCaseTwo(String currentNode, int[] instructions, Map<String, List<String>> nodes) {
        int steps = 0;
        while (!endsWithChar(currentNode, 'Z')) {
            for (int instruction : instructions) {
                steps++;
                currentNode = nodes.get(currentNode).get(instruction);
                if (endsWithChar(currentNode, 'Z')) {
                    break;
                }
            }
        }
        return steps;
    }

    private static boolean endsWithChar(String id, char Z) {
        return id.charAt(2) == Z;
    }

    private static Map<String, List<String>> getNodes(List<String> input) {
        Map<String, List<String>> nodes = new TreeMap<>();
        for (int i = 2; i < input.size(); i++) {
            Node node = NODE_PATTERN.matcher(input.get(i))
                    .results().map(e -> new Node(e.group(1), e.group(2), e.group(3))).findFirst().orElseThrow();
            nodes.put(node.id(), List.of(node.leftId(), node.rightId()));
        }
        return nodes;
    }

    public record Node(String id, String leftId, String rightId) {
    }

}
