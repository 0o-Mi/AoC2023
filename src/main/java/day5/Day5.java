package day5;


import day5.Day5.Seed.TYPE;
import utils.DayUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;

import static utils.RegexPatterns.NUMBER;


public class Day5 {

    public static void main(String[] args) {
        caseOne();
//        caseTwoBruteForce();
        caseTwo();
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(5, 1);
        List<String> input = dayUtils.getListInput();
        Map<Seed.TYPE, List<Conversion>> conversionsMap = getConversions(input);
        List<Conversion> locationConversions = conversionsMap.get(TYPE.location);
        Scope currentMinScope = locationConversions.stream().min(Comparator.comparing(e -> e.output)).orElseThrow().inputScope();
        for (int i = Seed.TYPE.values().length - 2; i >= 0; i--) {
            List<Conversion> rangeList = findAllConversionRange(conversionsMap.get(TYPE.values()[i]), currentMinScope);
        }

    }

    private static List<Conversion> findAllConversionRange(List<Conversion> conversions, Scope upperInput) {
        List<Conversion> sharedConversions = conversions.stream().map(conversion -> conversion.getSharedOutputConversion(upperInput)) // if output matches get shared part
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toCollection(ArrayList::new));// gets ranges that match the upperRange input
        List<Conversion> missingConversions = getMissingConversions(upperInput, sharedConversions);
        sharedConversions.addAll(missingConversions);
        sharedConversions.sort(Comparator.comparing(e -> e.output));
        return sharedConversions;
    }

    private static List<Conversion> getMissingConversions(Scope upperInput, List<Conversion> sharedConversions) {
        List<Conversion> missingConversions = new ArrayList<>();
        if (!sharedConversions.getLast().outputScope().expectedMin().equals(upperInput.expectedMin())) {
            Scope constScope = new Scope(upperInput.expectedMin(), sharedConversions.getLast().outputScope().expectedMin());
            missingConversions.add(new Conversion(
                    upperInput.expectedMin(),
                    constScope,
                    constScope
                    )); // wow actually what I wanted :) Thanks copilot
        }
        for (int i = 0; i < sharedConversions.size() - 1; i++) {
            Conversion conversion = sharedConversions.get(i);
            Conversion nextConversion = sharedConversions.get(i+1);
            if ((conversion.outputScope().expectedMax() + 1) != nextConversion.outputScope().expectedMin()) {
                Scope constScope = new Scope(conversion.outputScope().expectedMax(), nextConversion.outputScope().expectedMin());
                missingConversions.add(new Conversion(
                        conversion.outputScope().expectedMax(),
                        constScope,
                        constScope)
                );
            }
        }
        if (!sharedConversions.getLast().outputScope().expectedMax().equals(upperInput.expectedMax())) {
            Scope constScope = new Scope(sharedConversions.getLast().outputScope().expectedMax(), upperInput.expectedMax());
            missingConversions.add(new Conversion(
                    upperInput.expectedMax() - sharedConversions.getLast().outputScope().expectedMax(), //missing max
                    constScope,
                    constScope
            ));
        }
        return missingConversions;
    }


    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(5, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        List<TreeMap<Seed.TYPE, Long>> seeds = getSeeds(input);
        Map<Seed.TYPE, List<Conversion>> conversionsMap = getConversions(input);
        for (TreeMap<Seed.TYPE, Long> seed : seeds) {
            calculateSeedValues(seed, conversionsMap);
        }
        Long min = seeds.stream().map(s -> s.get(Seed.TYPE.location)).min(Long::compareTo).orElseThrow();
        TreeMap<Seed.TYPE, Long> minSeed = seeds.stream().min(Comparator.comparing(e -> e.get(Seed.TYPE.location))).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(min);
    }

    private static void caseTwoBruteForce() {
        DayUtils dayUtils = new DayUtils(5, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        Map<Seed.TYPE, List<Conversion>> conversionsMap = getConversions(input);
        List<Long> list = NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(Long::parseLong)
                .toList();
        Long minLocation = Long.MAX_VALUE;
        for (int i = 0; i < list.size(); i += 2) {
            System.out.println(i);
            Long rangeStart = list.get(i);
            for (
                    Long index = list.get(i);
                    index < rangeStart + list.get(i + 1);
                    index++
            ) {
                TreeMap<Seed.TYPE, Long> seed = new TreeMap<>(Map.of(Seed.TYPE.seed, index));
                calculateSeedValues(seed, conversionsMap);
                Long currentLocation = seed.get(Seed.TYPE.location);
                if (currentLocation.compareTo(minLocation) < 0) {
                    minLocation = currentLocation;
                }
            }
        }

        dayUtils.endTimer();
        dayUtils.printAnswer(minLocation);
    }

    private static List<TreeMap<Seed.TYPE, Long>> getSeeds(List<String> input) {
        return NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(Long::parseLong)
                .map((Long value) -> new TreeMap<>(Map.of(Seed.TYPE.seed, value)))
                .toList();
    }

    private static void calculateSeedValues(TreeMap<Seed.TYPE, Long> seed, Map<Seed.TYPE, List<Conversion>> conversionsMap) {
        for (int i = 0; i < Seed.TYPE.values().length - 1; i++) {
            Seed.TYPE type = Seed.TYPE.values()[i];
            Long seedValue = seed.get(type);
            List<Conversion> conversions = conversionsMap.get(type);
            Long newSeedValue = conversions.stream()
                    .filter(c -> c.isValueInRange(seedValue)).findAny()
                    .map(c -> c.getOutput(seedValue)).orElse(seedValue);
            seed.put(Seed.TYPE.values()[i + 1], newSeedValue);
        }
    }

    private static Map<Seed.TYPE, List<Conversion>> getConversions(List<String> input) {
        Map<Seed.TYPE, List<Conversion>> conversions = new HashMap<>();
        List<Conversion> currentConversions = new ArrayList<>();
        for (int i = 1; i < input.size(); i++) {
            if (input.get(i).isEmpty()) { // moves type by one
                i++;
                String fromTo = input.get(i);
                Seed.TYPE type = Seed.TYPE.valueOf(fromTo.substring(0, fromTo.indexOf("-")));
                conversions.put(type, currentConversions = new ArrayList<>());
            } else {
                List<Long> list = NUMBER.matcher(input.get(i)).results()
                        .map(MatchResult::group).map(Long::parseLong).toList();
                Long output = list.get(0);
                Long expectedMin = list.get(1);
                Long range = list.get(2);
                currentConversions.add(new Conversion(
                        output,
                        new Scope(expectedMin, expectedMin + range), // Min, Max
                        new Scope(output, output + range)
                ));
            }
        }
        return conversions;
    }

    public static class Seed {

        private Map<TYPE, Long> values;

        public Seed(Map<TYPE, Long> values) {
            this.values = values;
        }

        enum TYPE {
            seed,
            soil,
            fertilizer,
            water,
            light,
            temperature,
            humidity,
            location,
        }
    }


    public record Conversion(Long output, Scope inputScope, Scope outputScope) {

        public Long getOutput(Long input) {
            long exceedingAmount = input - this.inputScope.expectedMin;
            return this.output + exceedingAmount;
        }
        public boolean isValueInRange(Long input) {
            return this.inputScope.isValueInRange(input);
        }
        public Optional<Conversion> getSharedOutputConversion(Scope multiply) {
            Optional<Scope> optionalScope = this.outputScope.rangeMultiplication(multiply);

            if (optionalScope.isPresent()) {
                Scope sharedScope = optionalScope.get();
                Long range = sharedScope.expectedMax - sharedScope.expectedMin;
                long change = this.outputScope.expectedMin - sharedScope.expectedMin;
                Long output = this.output + change; // change due to multiply boundaries
                Long newInputMin = this.inputScope.expectedMin() + change;
                return Optional.of(new Conversion(output, new Scope(newInputMin, newInputMin + range), sharedScope));
            } else {
                return Optional.empty();
            }
        }
    }

    public record Scope(Long expectedMin, Long expectedMax) {
        public boolean isValueInRange(Long input) {
            return this.expectedMin() <= input && this.expectedMax() > input;
        }
        public Optional<Scope> rangeMultiplication(Scope multiply) {
            long sharedMin = Math.max(this.expectedMin, multiply.expectedMin);
            long sharedMax = Math.min(this.expectedMin, multiply.expectedMax);

            if (sharedMin <= sharedMax) {
                return Optional.of(new Scope(sharedMin, sharedMax));
            } else {
                return Optional.empty();
            }
        }
    }

}
