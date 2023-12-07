package day6;

import utils.DayUtils;
import utils.RegexPatterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class Day6 {
    public static void main(String[] args) {
        caseOne();
        caseTwo();
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

    private static void findWays(Race race, List<Integer> waysCount) {
        Set<Integer> distanceSet = new HashSet<>();
        for (int timeWaited = 0; timeWaited < race.time(); timeWaited++) {
            double distance = calculateDistance(race, timeWaited);
            if (distance > race.distance()) {
                distanceSet.add(timeWaited);
            }
        }
        waysCount.add(distanceSet.size());
    }

    private static double calculateDistance(Race race, double timeWaited) {
        return race.time() * timeWaited - Math.pow(timeWaited, 2);
    }


//    private static int calculateTimeWaited(Race race) {
//        int a = -1;
//        int b = race.time();
//        int c = -race.distance();
//        double delta = Math.pow(b, 2) - 4 * a * c;
//        System.out.println("delta: " + delta);
//        double sol1 = ((b + Math.sqrt(delta)) / (2 * a));
//        double sol2 = ((b - Math.sqrt(delta)) / (2 * a));
//        System.out.println(sol1 + " " + sol2);
//        calculateDistance(race, sol1);
//        calculateDistance(race, sol2);
//        return 0;
//    }


    private static List<Race> getRaces(List<String> input) {
        List<Integer> times = getNumbers(input.get(0));
        List<Integer> distances = getNumbers(input.get(1));
        List<Race> races = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            races.add(new Race(times.get(i), distances.get(i)));
        }
        return races;
    }

    private static List<Integer> getNumbers(String input) {
        Matcher matcher = RegexPatterns.NUMBER.matcher(input);
        return matcher.results().map(MatchResult::group).map(Integer::parseInt).toList();
    }

    public record Race(long time, long distance) {
    }
}
