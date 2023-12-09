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
        caseTwo();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(5, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        List<Seed> seeds = getSeeds(input);
        Map<TYPE, List<Conversion>> conversionsMap = getConversions(input);
        for (Seed seed : seeds) {
            calculateSeedValues(seed, conversionsMap);
        }
        Long min = seeds.stream().map(seed -> seed.get(TYPE.location)).min(Long::compareTo).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(min);
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(5, 2);
        List<String> input = dayUtils.getListInput();
        Map<TYPE, List<Conversion>> conversionsMap = getConversions(input);
        Long maxLocation = conversionsMap.get(TYPE.humidity).stream()
                .map(Conversion::inputScope).map(Scope::expectedMax).max(Long::compareTo).orElseThrow();
        getAndSetSeeds(input);
        Scope locationScope = new Scope(0L, maxLocation);
        recursivelyFind(TYPE.humidity.ordinal(), conversionsMap, locationScope);

        List<Seed> seeds = LOCATION_SCOPES.stream().map(Scope::expectedMin).map(Day5::getSeed).toList();
        for (Seed seed : seeds) {
            calculateSeedValues(seed, conversionsMap);
        }
        Long min = seeds.stream().map(seed -> seed.get(TYPE.location)).min(Long::compareTo).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(min);
    }

    private static Seed getSeed(Long e) {
        return new Seed(new TreeMap<>(Map.of(TYPE.seed, e)));
    }

    public static void setSeedScopes(List<Scope> seedScopes) {
        SEED_SCOPES = seedScopes;
    }

    private static List<Scope> SEED_SCOPES;

    private final static List<Scope> LOCATION_SCOPES = new ArrayList<>();

    private static void getAndSetSeeds(List<String> input) {
        List<Scope> caseTwoSeeds = getCaseTwoSeeds(input);
        setSeedScopes(caseTwoSeeds);
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

    private static void recursivelyFind(int currentConversionsType, Map<TYPE, List<Conversion>> conversionsMap, Scope upperInput) {
        if (currentConversionsType != -1) {
            List<Conversion> conversions = conversionsMap.get(TYPE.values()[currentConversionsType]);
            List<Conversion> allConversionRange = findAllConversionRange(conversions, upperInput);
            for (Conversion conversion : allConversionRange) {
                recursivelyFind(currentConversionsType - 1, conversionsMap, conversion.inputScope());
            }
        } else {
            for (Scope seedScope : SEED_SCOPES) {
                Optional<Scope> optionalScope = upperInput.rangeMultiplication(seedScope);
                optionalScope.ifPresent(LOCATION_SCOPES::add);
            }
        }
    }

    private static List<Conversion> findAllConversionRange(List<Conversion> conversions, Scope upperInput) {
        List<Conversion> sharedConversions = conversions.stream().map(conversion -> conversion.getSharedConversion(upperInput)) // to lowerOutput
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toCollection(ArrayList::new));// gets ranges that match the upperRange input

        if (sharedConversions.isEmpty()) {
            return List.of(new Conversion(upperInput, upperInput));
        }
        List<Conversion> missingConversions = getMissingConversions(upperInput, sharedConversions);
        sharedConversions.addAll(missingConversions);
        sharedConversions.sort(Comparator.comparing(e -> e.outputScope().expectedMin()));
        return sharedConversions;
    }

    private static List<Conversion> getMissingConversions(Scope upperInput, List<Conversion> sharedConversions) {
        List<Conversion> missingConversions = new ArrayList<>();
        adjustMin(upperInput, sharedConversions, missingConversions);
        adjustMax(upperInput, sharedConversions, missingConversions);
        sharedConversions.sort(Comparator.comparing(e -> e.outputScope().expectedMin()));
        adjustCenter(sharedConversions, missingConversions);
        return missingConversions;
    }

    private static void adjustCenter(List<Conversion> sharedConversions, List<Conversion> missingConversions) {
        for (int i = 0; i < sharedConversions.size() - 1; i++) {
            Conversion conversion = sharedConversions.get(i);
            Conversion nextConversion = sharedConversions.get(i + 1);
            Long max = conversion.outputScope().expectedMax();
            Long nextMin = nextConversion.outputScope().expectedMin();
            if (!max.equals(nextMin)) {
                Scope constScope = new Scope(max, nextMin);
                missingConversions.add(new Conversion(
                        constScope,
                        constScope
                ));
            }
        }
    }

    private static void adjustMax(Scope upperInput, List<Conversion> sharedConversions, List<Conversion> missingConversions) {
        Long currentEndMax = sharedConversions.stream().map(e -> e.outputScope().expectedMax()).max(Long::compareTo).orElseThrow();
        Long expectedEndMax = upperInput.expectedMax();
        if (!currentEndMax.equals(expectedEndMax)) {
            Scope constScope = new Scope(currentEndMax, expectedEndMax);
            missingConversions.add(new Conversion(
                    constScope,
                    constScope
            ));
        }
    }

    private static void adjustMin(Scope upperInput, List<Conversion> sharedConversions, List<Conversion> missingConversions) {
        Long expectedStartMin = upperInput.expectedMin();
        Long currentStartMin = sharedConversions.stream().map(e -> e.outputScope().expectedMin()).min(Long::compareTo).orElseThrow();
        if (!currentStartMin.equals(expectedStartMin)) {
            Scope constScope = new Scope(expectedStartMin, currentStartMin);
            missingConversions.add(new Conversion(
                    constScope,
                    constScope
            ));
        }
    }

    private static List<Seed> getSeeds(List<String> input) {
        return NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(Long::parseLong)
                .map(Day5::getSeed)
                .toList();
    }

    private static void calculateSeedValues(Seed seed, Map<TYPE, List<Conversion>> conversionsMap) {
        for (int i = 0; i < TYPE.values().length - 1; i++) {
            TYPE type = TYPE.values()[i];
            Long seedValue = seed.get(type);
            List<Conversion> conversions = conversionsMap.get(type);
            Long newSeedValue = conversions.stream()
                    .filter(c -> c.isValueInRange(seedValue)).findAny()
                    .map(c -> c.getOutput(seedValue)).orElse(seedValue);
            seed.conversions().put(TYPE.values()[i + 1], newSeedValue);
        }
    }

    private static Map<TYPE, List<Conversion>> getConversions(List<String> input) {
        Map<TYPE, List<Conversion>> conversions = new HashMap<>();
        List<Conversion> currentConversions = new ArrayList<>();
        for (int i = 1; i < input.size(); i++) {
            if (input.get(i).isEmpty()) { // moves type by one
                i++;
                String typeFrom = input.get(i);
                TYPE type = TYPE.valueOf(typeFrom.substring(0, typeFrom.indexOf("-")));
                conversions.put(type, currentConversions = new ArrayList<>());
            } else {
                List<Long> list = NUMBER.matcher(input.get(i)).results()
                        .map(MatchResult::group).map(Long::parseLong).toList();
                Long outputMin = list.get(0);
                Long expectedMin = list.get(1);
                Long range = list.get(2);
                currentConversions.add(new Conversion(
                        new Scope(expectedMin, expectedMin + range), // Min, Max
                        new Scope(outputMin, outputMin + range)
                ));
            }
        }
        return conversions;
    }

    public record Seed (Map<TYPE, Long> conversions) {
        public enum TYPE { //case one - from
            seed,
            soil,
            fertilizer,
            water,
            light,
            temperature,
            humidity,
            location,
        }

        public Long get(TYPE type) {
            return conversions().get(type);
        }
    }


    public record Conversion(Scope inputScope, Scope outputScope) {

        public Long getOutput(Long input) {
            long exceedingAmount = input - this.inputScope.expectedMin;
            return this.outputScope().expectedMin + exceedingAmount;
        }

        public boolean isValueInRange(Long input) {
            return this.inputScope.isValueInRange(input);
        }

        public Optional<Conversion> getSharedConversion(Scope upperInput) { // on lower
            Optional<Scope> optionalScope = this.outputScope.rangeMultiplication(upperInput);
            if (optionalScope.isPresent()) {
                Scope sharedScope = optionalScope.get();
                long range = sharedScope.expectedMax - sharedScope.expectedMin;
                long change = sharedScope.expectedMin - this.outputScope.expectedMin;
                long newExpectedMin = inputScope.expectedMin + change;
                Scope inputScope = new Scope(newExpectedMin, newExpectedMin + range);
                return Optional.of(new Conversion(inputScope, sharedScope)); // going to get lost anyway (can be refactored for calculation)
            } else {
                return Optional.empty();
            }
        }

        @Override
        public String toString() {
            return "C{" +
                    ", iS=" + inputScope +
                    ", oS=" + outputScope +
                    '}';
        }
    }

    public record Scope(Long expectedMin, Long expectedMax) {
        public boolean isValueInRange(Long input) {
            return this.expectedMin <= input && this.expectedMax > input;
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
