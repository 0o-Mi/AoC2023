package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DayUtils {

    public static String RESOURCE_PATH = "MyAoC/src/main/resources/";
    private final Path inputPath;
    private final int day;
    private final int caseNumber;

    private static long timeStart;
    private static long timeEnd;

    public DayUtils(int day, int caseNumber, FetchOption fetchOption) {
        this.day = day;
        this.caseNumber = caseNumber;
        this.inputPath = Path.of(RESOURCE_PATH + "day" + day + ".txt");
        InputFetcher inputFetcher = new InputFetcher(inputPath, day, fetchOption);
        inputFetcher.loadInputData();
    }

    public DayUtils(int day, int caseNumber) {
        this(day, caseNumber, FetchOption.FETCH_IF_EMPTY);
    }


    public String getStringInput() {
        if (Files.exists(inputPath)) {
            try {
                return Files.readString(inputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Input file not found");
        }
    }

    public List<String> getListInput() {
        if (Files.exists(inputPath)) {
            try {
                return Files.readAllLines(inputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Input file not found");
        }
    }

    public void startTimer() {
        timeStart = System.nanoTime();
    }

    public void endTimer() {
        timeEnd = System.nanoTime();
    }

    public void printAnswer(Object answer) {
        System.out.printf(
                "Day: [%s] Case: [%s] Time: [%s] Answer[%s]%n",
                day, caseNumber, getFinalTime(), answer.toString()
        );
    }

    public String getFinalTime() {
        long timeSpent = (timeEnd - timeStart) /1000;
        if (timeSpent < 1000)
            return timeSpent + "μs";
        if (timeSpent < 1000000)
            return (timeSpent / 1000.0) + "ms";
        return (timeSpent / 1000000.0) + "s";
    }

    public enum FetchOption {
        FETCH_IF_EMPTY,
        RE_FETCH,
        DONT_FETCH
    }
}
