package day9;

import utils.DayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;

import static utils.RegexPatterns.NUMBER;

public class Day9 {
    public static void main(String[] args) {
        caseOne();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(9, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        List<Prediction> predictions = getPredictions(input);
        List<Long> predicted = predictNextNumbers(predictions);
        Long sum = predicted.stream().reduce(Long::sum).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static List<Long> predictNextNumbers(List<Prediction> predictions) {
        List<Long> predicted = new ArrayList<>();
        for (Prediction prediction : predictions) {
            prediction.numberRows().getLast().add(0L);
            long predictedNumber = 0L;
            for (int i = prediction.numberRows().size() - 1; i > 0; i--) {
                List<Long> lowerRow = prediction.numberRows().get(i);
                List<Long> upperRow = prediction.numberRows().get(i - 1);
                Long lowerLast = lowerRow.getLast();
                Long upperLast = upperRow.getLast();
                predictedNumber = lowerLast + upperLast;
                upperRow.add(predictedNumber);
            }
            predicted.add(predictedNumber);
        }
        return predicted;
    }

    private static List<Prediction> getPredictions(List<String> input) {
        List<Prediction> predictions = new ArrayList<>();
        for (String s : input) {
            ArrayList<Long> numbers = NUMBER.matcher(s).results().map(MatchResult::group).map(Long::parseLong).collect(Collectors.toCollection(ArrayList::new));
            Prediction prediction = new Prediction(new ArrayList<>());
            prediction.numberRows().add(numbers);
            while (numbers.stream().reduce(Long::sum).orElseThrow() != 0L) {
                numbers = getDifferences(numbers);
                prediction.numberRows().add(numbers);
                if (numbers.size() == 1) {
                    prediction.numberRows().add(new ArrayList<>(List.of(0L)));
                    break;
                }
            }
            System.out.println(prediction);
            predictions.add(prediction);
        }
        return predictions;
    }

    private static ArrayList<Long> getDifferences(ArrayList<Long> numbers) {
        ArrayList<Long> differences = new ArrayList<>();
        for (int i = 1; i < numbers.size(); i++) {
            differences.add(Math.abs(numbers.get(i) - numbers.get(i - 1)));
        }
        return differences;
    }

    private record Prediction(List<ArrayList<Long>> numberRows) {
    }
}

