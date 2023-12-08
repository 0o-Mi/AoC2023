package day8;

import com.sun.source.tree.Tree;
import utils.DayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
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
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(8, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        int[] instructions = input.getFirst().chars().map(key -> INSTRUCTION_MAP.get((char) key)).toArray();
        System.out.println(Arrays.toString(instructions));
        Map<String, List<String>> nodes = new TreeMap<>();
        for (int i = 2; i < input.size(); i++) {
            Node node = NODE_PATTERN.matcher(input.get(i))
                    .results().map(e -> new Node(e.group(1), e.group(2), e.group(3))).findFirst().orElseThrow();
            nodes.put(node.id(), List.of(node.leftId(), node.rightId()));
        }
        String currentNode = "AAA";
        int steps = 0;
        while (!currentNode.equals("ZZZ")) {
            for (int instruction : instructions) {
                steps++;
                currentNode = nodes.get(currentNode).get(instruction);
                if (currentNode == "ZZZ") {
                    break;
                }
            }
        }
        nodes.forEach((k,v) -> System.out.println(k + " " + v));
        dayUtils.endTimer();
        dayUtils.printAnswer(steps);
    }

    public record Node(String id, String leftId, String rightId) {
    }

}
