package day2;

import utils.DayUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2 {
    public static final int MAX_RED = 12;
    public static final int MAX_GREEN = 13;
    public static final int MAX_BLUE = 14;

    public static void main(String[] args) {
        partOne();
        partTwo();
    }
    private static void partOne() {
        DayUtils dayUtils = new DayUtils(2, 1);
        dayUtils.startTimer();
        Integer i = dayUtils.getListInput().stream()
                .map(e -> e.split(";"))
                .map(Day2::extractSummarizedGame)
                .filter(round -> round.red <= MAX_RED && round.green <= MAX_GREEN && round.blue <= MAX_BLUE)
                .map(game -> game.gameNumber)
                .reduce(Integer::sum)
                .orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(i);
    }

    private static void partTwo() {
        DayUtils dayUtils = new DayUtils(2, 2);
        dayUtils.startTimer();
        Integer i = dayUtils.getListInput().stream()
                .map(e -> e.split(";"))
                .map(Day2::extractSummarizedGame)
                .map(e -> e.red * e.green * e.blue)
                .reduce(Integer::sum)
                .orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(i);
    }

    // turns the roundsString into a SummarizedGame - maximum color values
    private static SummarizedGame extractSummarizedGame(String[] strings) {
        int gameNumber = extractGameNumber(strings);
        List<String[]> rounds = Arrays.stream(strings).map(e -> e.split(",")).toList();
        Integer red = getMaxSummarizedColorValue(rounds, "red");
        Integer green = getMaxSummarizedColorValue(rounds, "green");
        Integer blue = getMaxSummarizedColorValue(rounds, "blue");
        return new SummarizedGame(gameNumber, red, green, blue);
    }

    // finds the maximum number of a color in String rounds
    private static Integer getMaxSummarizedColorValue(List<String[]> rounds, String color) {
        return rounds.stream().map(array -> Arrays.stream(array)
                .filter(colorValue -> colorValue.contains(color)) // filters values in round to one color
                .map(colorValue -> Integer.parseInt(colorValue.substring(0, colorValue.lastIndexOf(' ')).strip())) // extracts the color
                .findFirst().orElse(0)).max(Comparator.comparingInt(e -> e)).orElseThrow(); // finds the maximum
    }

    // finds and deletes the game number
    private static final Pattern gamePattern = Pattern.compile("Game (\\d+):");
    private static int extractGameNumber(String[] games) {
        Matcher matcher = gamePattern.matcher(games[0]);
        int gameNumber = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
        games[0] = games[0].split(":")[1]; // gets rid of gameNumber
        return gameNumber;
    }

    public record SummarizedGame(int gameNumber, int red, int green, int blue) {
    }
}
