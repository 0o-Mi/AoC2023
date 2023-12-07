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

    public static void setSeedScopes(List<Scope> seedScopes) {
        SEED_SCOPES = seedScopes;
    }

    private static List<Scope> SEED_SCOPES;
    private final static List<Scope> LOCATION_SCOPES = new ArrayList<>();

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(5, 2);
        List<String> input = dayUtils.getListInput();
        Map<Seed.TYPE, List<Conversion>> conversionsMap = getConversions(input);
        List<Conversion> locationConversions = conversionsMap.get(TYPE.humidity);
        Long maxLocation = locationConversions.stream().map(e -> e.inputScope().expectedMax()).max(Long::compareTo).orElseThrow();
//        System.out.println(recursivelyFind(locationConversions));
        getAndSetSeeds(input);
//        Scope scope = new Scope(93L,97L);
//        Scope scope = new Scope(0L,56L)
//        Scope scope = new Scope(56L,93L);
        Scope scope = new Scope(0L,maxLocation);
        recursivelyFind(TYPE.humidity.ordinal(), conversionsMap, scope);

        List<TreeMap<TYPE, Long>> seeds = LOCATION_SCOPES.stream().map(Scope::expectedMin).map(e -> new TreeMap<>(Map.of(TYPE.seed, e))).toList();
        for (TreeMap<Seed.TYPE, Long> seed : seeds) {
            calculateSeedValues(seed, conversionsMap);
        }
        Long min = seeds.stream().map(s -> s.get(Seed.TYPE.location)).min(Long::compareTo).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(min);
//        TreeMap<TYPE, Long> seed = new TreeMap<>(Map.of(TYPE.seed, 82L));
//        calculateSeedValues(seed, conversionsMap);
//        System.out.println(seed.get(TYPE.location));

    }

    private static void getAndSetSeeds(List<String> input) {
        List<Scope> caseTwoSeeds = getCaseTwoSeeds(input);
        setSeedScopes(caseTwoSeeds);
//        System.out.println("SEED_SCOPES: " + SEED_SCOPES);
    }

    private static List<Scope> getCaseTwoSeeds(List<String> input) {
        List<Scope> scopes = new ArrayList<>();
        List<Long> longs = NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(Long::parseLong)
                .toList();
        for (int i = 0; i < longs.size(); i += 2) {
            Long min = longs.get(i);
            Long max = longs.get(i + 1);
            scopes.add(new Scope(min, min + max));
        }
        return scopes;
    }

    private static void recursivelyFind(int currentConversionsType, Map<Seed.TYPE, List<Conversion>> conversionsMap, Scope upperInput) {
//        System.out.println("upperInput: " + upperInput + " currentConversionsType: " + Seed.TYPE.values()[currentConversionsType + 1]);
        if (currentConversionsType != -1) {
            List<Conversion> conversions = conversionsMap.get(Seed.TYPE.values()[currentConversionsType]);
//            System.out.println("conversions: " + conversions);
            List<Conversion> allConversionRange = findAllConversionRange(conversions, upperInput);
//            System.out.println("allConversionRange: " + allConversionRange);
//            System.out.println("conversions.size(): " + allConversionRange.size());
            for (Conversion conversion : allConversionRange) {
                recursivelyFind(currentConversionsType - 1, conversionsMap, conversion.inputScope());
            }
        } else {
//            System.out.println("seedCandidate: " + upperInput);
            for (Scope seedScope : SEED_SCOPES) {
//                System.out.println("optionalScope: " + optionalScope);
                Optional<Scope> optionalScope = upperInput.rangeMultiplication(seedScope);
                optionalScope.ifPresent(scope -> LOCATION_SCOPES.add(scope));
            }
        }
    }

    // i have this upperInput. which of you will provide it?
    private static List<Conversion> findAllConversionRange(List<Conversion> conversions, Scope upperInput) {
        List<Conversion> sharedConversions = conversions.stream().map(conversion -> conversion.getSharedConversion(upperInput)) // to lowerOutput
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toCollection(ArrayList::new));// gets ranges that match the upperRange input

        if (sharedConversions.isEmpty()) {
            return List.of(new Conversion(upperInput.expectedMin(), upperInput, upperInput));
        }
        List<Conversion> missingConversions = getMissingConversions(upperInput, sharedConversions);
        sharedConversions.addAll(missingConversions);
        sharedConversions.sort(Comparator.comparing(e -> e.outputScope().expectedMin()));
//        System.out.println("sharedConversions:" + sharedConversions);
//        sharedConversions.sort(Comparator.comparing(Conversion::output));

        return sharedConversions;
    }

    private static List<Conversion> getMissingConversions(Scope upperInput, List<Conversion> sharedConversions) {
        List<Conversion> missingConversions = new ArrayList<>();
        Long expectedStartMin = upperInput.expectedMin();
        Long currentStartMin = sharedConversions.stream().map(e -> e.outputScope().expectedMin()).min(Long::compareTo).orElseThrow();
        if (!currentStartMin.equals(expectedStartMin)) {
            Scope constScope = new Scope(expectedStartMin, currentStartMin);
            missingConversions.add(new Conversion(
                    expectedStartMin,
                    constScope,
                    constScope
            ));
        }
        Long currentEndMax = sharedConversions.stream().map(e -> e.outputScope().expectedMax()).max(Long::compareTo).orElseThrow();
        Long expectedEndMax = upperInput.expectedMax();
        if (!currentEndMax.equals(expectedEndMax)) {
            Scope constScope = new Scope(currentEndMax, expectedEndMax);
            missingConversions.add(new Conversion(
                    currentEndMax,
                    constScope,
                    constScope
            ));
        }
        sharedConversions.sort(Comparator.comparing(e -> e.outputScope().expectedMin()));
        for (int i = 0; i < sharedConversions.size() - 1; i++) {
            Conversion conversion = sharedConversions.get(i);
            Conversion nextConversion = sharedConversions.get(i + 1);
            // if hole
            Long max = conversion.outputScope().expectedMax();
            Long nextMin = nextConversion.outputScope().expectedMin();
            if (!max.equals(nextMin)) {
                Scope constScope = new Scope(max, nextMin);
                missingConversions.add(new Conversion(
                        max,
                        constScope,
                        constScope
                ));
            }
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
//        seeds.stream().map(e -> e.get(Seed.TYPE.location)).forEach(System.out::println);
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

        public Optional<Conversion> getSharedConversion(Scope upperInput) { // on lower
            Optional<Scope> optionalScope = this.outputScope().rangeMultiplication(upperInput);
            if (optionalScope.isPresent()) {
                Scope sharedScope = optionalScope.get();
                Long range = sharedScope.expectedMax - sharedScope.expectedMin;
                long change = sharedScope.expectedMin - this.outputScope.expectedMin;

                // change due to upperInput boundaries
                // sharedScope is smaller than this.outputScope
                // change sharedScore into adjusted inputScope


                long newExpectedMin = inputScope().expectedMin + change;
                Scope inputScope = new Scope(newExpectedMin, newExpectedMin + range);
                return Optional.of(new Conversion(null, inputScope, sharedScope)); // going to get lost anyway
            } else {
                return Optional.empty();
            }
        }

        @Override
        public String toString() {
            return "C{" +
                    "o=" + output +
                    ", iS=" + inputScope +
                    ", oS=" + outputScope +
                    '}';
        }
    }

    public record Scope(Long expectedMin, Long expectedMax) {
        public boolean isValueInRange(Long input) {
            return this.expectedMin() <= input && this.expectedMax() > input;
        }

        @Override
        public String toString() {
            return "{" +
                    "mi=" + expectedMin +
                    ", ma=" + expectedMax +
                    '}';
        }

        public Optional<Scope> rangeMultiplication(Scope multiply) {
            long sharedMin = Math.max(this.expectedMin, multiply.expectedMin);
            long sharedMax = Math.min(this.expectedMax, multiply.expectedMax);

            if (sharedMin <= sharedMax) {
                return Optional.of(new Scope(sharedMin, sharedMax));
            } else {
                return Optional.empty();
            }


        }
    }

}
