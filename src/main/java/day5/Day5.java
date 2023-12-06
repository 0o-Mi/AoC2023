package day5;


import utils.DayUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.MatchResult;

import static utils.RegexPatterns.NUMBER;


public class Day5 {

    public static void main(String[] args) {
//        caseOne();
        caseTwo();
//        calculateCaseTwoTotalSeedNumber();
    }

    private static void calculateCaseTwoTotalSeedNumber() {
        DayUtils dayUtils = new DayUtils(5, 1);
        List<String> input = dayUtils.getListInput();
        List<BigInteger> list = NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(BigInteger::new)
                .toList();
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < list.size(); i += 2) {
            sum = sum.add(list.get(i));
        }
        System.out.println(sum);
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(5, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        List<TreeMap<TYPE, BigInteger>> seeds = getCaseOneSeeds(input);
        Map<TYPE, List<Conversion>> conversionsMap = getConversions(input);
        for (TreeMap<TYPE, BigInteger> seed : seeds) {
            calculateSeedValues(seed, conversionsMap);
        }
        BigInteger min = seeds.stream().map(s -> s.get(TYPE.location)).min(BigInteger::compareTo).orElseThrow();
        TreeMap<TYPE, BigInteger> minSeed = seeds.stream().min(Comparator.comparing(e -> e.get(TYPE.location))).orElseThrow();
        System.out.println(minSeed);
        dayUtils.endTimer();
        dayUtils.printAnswer(min);
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(5, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
//        List<TreeMap<TYPE, BigInteger>> seeds = new ArrayList<>();
        Map<TYPE, List<Conversion>> conversionsMap = getConversions(input);
        List<BigInteger> list = NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(BigInteger::new)
                .toList();
        BigInteger minLocation = new BigInteger("1100000000000000000000000000");
        for (int i = 0; i < list.size(); i += 2) {
            System.out.println(i);
            BigInteger rangeStart = list.get(i);
            for (
                    BigInteger index = list.get(i);
                    index.compareTo(rangeStart.add(list.get(i + 1))) < 0;
                    index = index.add(BigInteger.ONE)
            ) {
                TreeMap<TYPE, BigInteger> seed = new TreeMap<>(Map.of(TYPE.seed, index));
                calculateSeedValues(seed, conversionsMap);
                BigInteger currentLocation = seed.get(TYPE.location);
                if (currentLocation.compareTo(minLocation) < 0) {
                    minLocation = currentLocation;
                }
            }
        }

        dayUtils.endTimer();
        dayUtils.printAnswer(minLocation);
    }

    private static List<TreeMap<TYPE, BigInteger>> getCaseOneSeeds(List<String> input) {
        return NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(BigInteger::new)
                .map((BigInteger value) -> new TreeMap<>(Map.of(TYPE.seed, value)))
                .toList();
    }

    private static List<TreeMap<TYPE, BigInteger>> getCaseTwoSeeds(List<String> input) {
        List<TreeMap<TYPE, BigInteger>> seeds = new ArrayList<>();
        List<BigInteger> list = NUMBER.matcher(input.get(0)).results()
                .map(MatchResult::group)
                .map(BigInteger::new)
                .toList();
        for (int i = 0; i < list.size(); i += 2) {
            BigInteger rangeStart = list.get(i);
            for (
                    BigInteger index = list.get(i);
                    index.compareTo(rangeStart.add(list.get(i + 1))) < 0;
                    index = index.add(BigInteger.ONE)
            ) {
                seeds.add(new TreeMap<>(Map.of(TYPE.seed, index)));
            }
        }
        return seeds;
    }

    private static void calculateSeedValues(TreeMap<TYPE, BigInteger> seed, Map<TYPE, List<Conversion>> conversionsMap) {
        for (int i = 0; i < TYPE.values().length - 1; i++) {
            TYPE type = TYPE.values()[i];
            BigInteger seedValue = seed.get(type);
            List<Conversion> conversions = conversionsMap.get(type);
            BigInteger newSeedValue = conversions.stream()
                    .filter((Conversion conversion) -> seedValue.compareTo(conversion.expectedMin()) >= 0 &&
                            seedValue.compareTo(conversion.expectedMax) < 0).findAny()
                    .map(e -> e.output().add(seedValue.subtract(e.expectedMin()))).orElse(seedValue);
            seed.put(TYPE.values()[i + 1], newSeedValue);
        }
    }

    private static Map<TYPE, List<Conversion>> getConversions(List<String> input) {
        Map<TYPE, List<Conversion>> conversions = new HashMap<>();
        List<Conversion> currentConversions = new ArrayList<>();
        for (int i = 1; i < input.size(); i++) {
            if (input.get(i).isEmpty()) { // moves type by one
                i++;
                String fromTo = input.get(i);
                TYPE type = TYPE.valueOf(fromTo.substring(0, fromTo.indexOf("-")));
                conversions.put(type, currentConversions = new ArrayList<>());
            } else {
                List<BigInteger> list = NUMBER.matcher(input.get(i)).results()
                        .map(MatchResult::group).map(BigInteger::new).toList();
                currentConversions.add(new Conversion(
                        list.get(0),
                        list.get(1),
                        list.get(1).add(list.get(2)) // calculate expectedMax
                ));
            }
        }
        return conversions;
    }

    public static class Seed {
        private Map<TYPE, BigInteger> values;

        public Seed(Map<TYPE, BigInteger> values) {
            this.values = values;
        }

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


    public record Conversion(BigInteger output, BigInteger expectedMin, BigInteger expectedMax) {
    }

}
