package day6;

import utils.DayUtils;
import utils.RegexPatterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class Day6 {
    public static void main(String[] args) {
        caseOne();
        caseTwo();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(6, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        List<Race> races = getRaces(input);
        List<Integer> waysCount = new ArrayList<>();
        for (Race race : races) {
            findWays(race, waysCount);
        }
        int multiply = waysCount.stream().reduce((a, b) -> a * b).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(multiply);
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(6, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        Race race = getRace(input);
        List<Integer> waysCount = new ArrayList<>();
        findWays(race, waysCount);
        int multiply = waysCount.stream().reduce((a, b) -> a * b).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(multiply);
    }

    private static Race getRace(List<String> input) {
        String time = getNumbers(input.get(0)).stream().map(Object::toString).reduce((l, r) -> l + r).orElseThrow();
        String distance = getNumbers(input.get(1)).stream().map(Object::toString).reduce((l, r) -> l + r).orElseThrow();
        System.out.println(time + " " + distance);
        return new Race(Long.parseLong(time), Long.parseLong(distance));
    }

    private static List<Race> getRaces(List<String> input) {
        List<Integer> times = getNumbers(input.get(0));
        List<Integer> distances = getNumbers(input.get(1));
        List<Race> races = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            races.add(new Race(times.get(i), distances.get(i)));
        }
        return races;
    }

    private static void findWays(Race race, List<Integer> waysCount) {
        Set<Integer> distanceSet = new HashSet<>();
        for (int timeWaited = 0; timeWaited < race.holdTime(); timeWaited++) {
            double distance = calculateDistance(race, timeWaited);
            if (distance > race.distance()) {
                distanceSet.add(timeWaited);
            }
        }
        waysCount.add(distanceSet.size());
    }

    private static double calculateDistance(Race race, double timeWaited) {
        return race.holdTime() * timeWaited - Math.pow(timeWaited, 2);
    }

    private static List<Integer> getNumbers(String input) {
        Matcher matcher = RegexPatterns.NUMBER.matcher(input);
        return matcher.results().map(MatchResult::group).map(Integer::parseInt).toList();
    }

    private static long solveHoldTime(Race race) {
        long a = -1;
        long b = race.holdTime();
        long c = -race.distance();
        double delta = Math.sqrt(b * b - 4 * a * c);
        double sol1 = (b + delta) / 2; // why not multiply by a ????!?!
        double sol2 = (b - delta) / 2; // I'm confused
        System.out.println(sol1 + " " + sol2);
        double range = sol1 - sol2;
        System.out.println(calculateDistance(race, sol1));
        System.out.println(calculateDistance(race, sol2));
        return (long) (
                sol1 - Math.ceil(sol2) + 1 - 2 * ((delta % 1) == 0 ? 1 : 0)
        );
    }

    public record Race(long holdTime, long distance) {
    }
}
