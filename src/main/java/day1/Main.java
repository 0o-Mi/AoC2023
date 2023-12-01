package day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Path.of("src/main/java/day1/calibrationdocument.txt");
        partOne(path);
        partTwo(path);
    }

    private static void partTwo(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<String> lines = Files.lines(path);) {
                var s = lines
                        .map(Main::getNumbers)
                        .map(List::toArray)
                        .map(array -> String.valueOf(array[0]) + array[array.length - 1])
                        .map(Integer::parseInt)
                        .reduce(Integer::sum)
                        .orElseThrow();
                System.out.println("Part 2: " + s);
            }
        }
    }

    private static List<Integer> getNumbers(String line) {
        List<Integer> numberList = new ArrayList<>();
        Map<Integer, String> occurrences = new TreeMap<>();
        int index = 0;
        for (var entry : stringToInteger.entrySet()) {
            while (line.substring(index).contains(entry.getKey())) {
                index = line.indexOf(entry.getKey(), index);
                occurrences.put(index, entry.getKey());
                index++;
            }
            index = 0;
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

    private static void partOne(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<String> lines = Files.lines(path);) {
                var s = lines
                        .map(String::chars)
                        .map(e -> e.map(Character::getNumericValue))
                        .map(intStream -> intStream.filter(character -> character >= 1 && character <= 9))
                        .map(IntStream::toArray)
                        .filter(e -> e.length > 0)
                        .map(array -> String.valueOf(array[0]) + array[array.length - 1])
                        .map(Integer::parseInt)
                        .reduce(Integer::sum)
                        .orElseThrow();
                System.out.println("Part 1: " + s);
            }
        }
    }
}
