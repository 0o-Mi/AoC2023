package day7;

import utils.DayUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day7 {

    public static void main(String[] args) {
        caseOne();
        caseTwo();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(7, 1);
        dayUtils.startTimer();
        List<String> input = dayUtils.getListInput();
        AtomicInteger bidRank = new AtomicInteger(1);
        int sum = input.stream().map(
                line -> getBid(line, cards -> 0, Hand.cardToValue))
                .sorted(bidComparator).map(e -> e.bidAmount() * bidRank.getAndIncrement())
                .reduce(Integer::sum).orElseThrow();

        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(7, 2);
        dayUtils.startTimer();
        List<String> input = dayUtils.getListInput();
        AtomicInteger bidRank = new AtomicInteger(1);
        int sum = input.stream().map(
                line -> getBid(line, cards -> (int) cards.stream().filter(e -> e == 1).count(), Hand.cardToValueWithJoker))
                .sorted(bidComparator).map(e -> e.bidAmount() * bidRank.getAndIncrement())
                .reduce(Integer::sum).orElseThrow();

        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static Bid getBid(String input, Function<List<Integer>, Integer> jokerCountFunction, Map<Character, Integer> cardToValue) {
        String[] array = input.split(" ");
        String stringHand = array[0];
        int bidAmount = Integer.parseInt(array[1]);

        List<Integer> cards = stringHand.chars().map(e -> cardToValue.get((char) e)).boxed().toList();
        Integer jokerCount = jokerCountFunction.apply(cards);
        List<Count> counts = jokerCount == 5 ? List.of(new Count(5, 14)) : getCounts(cards);

        return new Bid(getHand(counts, cards, jokerCount), bidAmount);
    }

    private static List<Count> getCounts(List<Integer> cards) {
        Map<Integer, Long> cardsByCount = cards.stream().collect(Collectors.groupingBy(
                e -> e,
                Collectors.counting()));
        return cardsByCount.entrySet().stream().map(e -> new Count(e.getValue().intValue(), e.getKey()))
                .sorted(Comparator.comparing(Count::count).reversed()
                        .thenComparing(Count::cardValue)
                ).toList();
//                Map.Entry.<Integer, Long>comparingByValue(Comparator.reverseOrder())
//                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder()))
    }

    private static Hand getHand(List<Count> counts, List<Integer> cards, int jokerCount) {
        Count highest = counts.getFirst();
        Hand.HandType handType = Hand.HandType.integerToHandType.get(jokerCount + highest.count());
        if (handType == Hand.HandType.ONE && counts.get(1).count() == 2) {
                handType = Hand.HandType.TWO;
        } else if (handType == Hand.HandType.THREE && counts.get(1).count() == 2) {
            handType = Hand.HandType.HOUSE;
        }
        return new Hand(cards, handType);
    }

    private static final Comparator<Bid> bidComparator = Comparator.comparingInt((Bid bid) -> bid.hand().handType().ordinal()).reversed()
            .thenComparing((Bid bid) -> bid.hand().cards.get(0))
            .thenComparing((Bid bid) -> bid.hand().cards.get(1))
            .thenComparing((Bid bid) -> bid.hand().cards.get(2))
            .thenComparing((Bid bid) -> bid.hand().cards.get(3))
            .thenComparing((Bid bid) -> bid.hand().cards.get(4));
//                .thenComparing((Bid bid) -> bid.hand().value()[0])
//                .thenComparing((Bid bid) -> bid.hand().value().length > 1 ? bid.hand().value()[1] : 0)

    public record Count(int count, int cardValue) {
    }

    public record Bid(Hand hand, int bidAmount) {
    }

    public record Hand(List<Integer> cards, HandType handType) {

        public enum HandType {
            FIVE,
            FOUR,
            HOUSE,
            THREE,
            TWO,
            ONE,
            HIGH;

            public static final Map<Integer, HandType> integerToHandType = Map.of(
                    5, HandType.FIVE,
                    4, HandType.FOUR,
                    3, HandType.THREE,
                    2, HandType.ONE,
                    1, HandType.HIGH
            );
        }

        public static final Map<Character, Integer> cardToValue = new TreeMap<>(Map.of(
                'T', 10, '9', 9, '8', 8, '7', 7, '6', 6, '5', 5, '4', 4, '3', 3, '2', 2
        ));

        public static final Map<Character, Integer> cardToValueWithJoker = new TreeMap<>();

        static {
            cardToValueWithJoker.putAll(cardToValue);
            cardToValue.putAll(Map.of(
                    'A', 14, 'K', 13, 'Q', 12, 'J', 11
            ));

            cardToValueWithJoker.putAll(Map.of(
                    'A', 14, 'K', 13, 'Q', 12, 'J', 1
            ));
        }

    }
}
