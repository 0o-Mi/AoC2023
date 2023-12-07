package day7;

import utils.DayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Day7 {

    public static void main(String[] args) {
//        caseOne();
        caseTwo();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(7, 1);
        dayUtils.startTimer();
        List<String> input = dayUtils.getListInput();
        List<Bid> bids = getBids(input);

        int sum = getSum(bids);

        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static void caseTwo() {
        DayUtils dayUtils = new DayUtils(7, 2);
        dayUtils.startTimer();
        List<String> input = dayUtils.getListInput();
        List<Bid> bids = getCaseTwoBids(input);

        int sum = getSum(bids);

        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static int getSum(List<Bid> bids) {
        bids.sort(Comparator.comparingInt((Bid bid) -> bid.hand().handType().ordinal()).reversed()
                        .thenComparing((Bid bid) -> bid.hand().cards[0])
                        .thenComparing((Bid bid) -> bid.hand().cards[1])
                        .thenComparing((Bid bid) -> bid.hand().cards[2])
                        .thenComparing((Bid bid) -> bid.hand().cards[3])
                        .thenComparing((Bid bid) -> bid.hand().cards[4])
//                .thenComparing((Bid bid) -> bid.hand().value()[0])
//                .thenComparing((Bid bid) -> bid.hand().value().length > 1 ? bid.hand().value()[1] : 0)
        );
        int sum = 0;
        for (int j = 1; j <= bids.size(); j++) {
            sum += bids.get(j - 1).bidAmount() * j;
        }
        bids.forEach(System.out::println);
        return sum;
    }

    private static List<Bid> getCaseTwoBids(List<String> input) {
        List<Bid> bids = new ArrayList<>();
        for (String s : input) {
            String[] array = s.split(" ");
            String stringHand = array[0];
            int bidAmount = Integer.parseInt(array[1]);

            int[] notSortedCards = stringHand.chars().map(e -> Hand.cardToValueWithJoker.get((char) e)).toArray();
            int jokerCount = (int) Arrays.stream(notSortedCards).filter(e -> e == 1).count();
            int[] cardsWithoutJoker = Arrays.stream(notSortedCards).filter(e -> e != 1).sorted().toArray();
            List<Count> counts;
            if (cardsWithoutJoker.length == 0) {
                counts = new ArrayList<>(List.of(new Count(5, 14)));
            } else {
                counts = getCounts(cardsWithoutJoker, 1 + jokerCount);
            }

            Bid bid = getBid(counts, notSortedCards, bidAmount, jokerCount);
//            System.out.println(bid);
            bids.add(bid);
        }
        return bids;
    }

    private static List<Bid> getBids(List<String> input) {
        List<Bid> bids = new ArrayList<>();
        for (String s : input) {
            String[] array = s.split(" ");
            String stringHand = array[0];
            int bidAmount = Integer.parseInt(array[1]);

            int[] notSortedCards = stringHand.chars().map(e -> Hand.cardToValue.get((char) e)).toArray();
            int[] cards = Arrays.stream(notSortedCards).sorted().toArray();
            List<Count> counts = getCounts(cards, 1);

            Bid bid = getBid(counts, notSortedCards, bidAmount, 0);
            bids.add(bid);
        }
        return bids;
    }

    private static List<Count> getCounts(int[] cards, int defaultCount) {
        int count = defaultCount;
        List<Count> counts = new ArrayList<>();

        int previous = cards[0];
        for (int i = 1; i < cards.length; i++) {
            if (cards[i] == previous) {
                count++;
            } else {
                counts.add(new Count(count, cards[i - 1]));
                count = defaultCount;
            }
            previous = cards[i];
        }
        counts.add(new Count(count, cards[cards.length - 1]));
        return counts;
    }

    private static Bid getBid(List<Count> counts, int[] notSortedCards, int bid, int jokerCount) {
        counts.sort(Comparator.comparing(Count::count).thenComparing(Count::value).reversed());
        Count highest = counts.getFirst();
        Hand.HandType handType = Hand.HandType.integerToHandType.get(highest.count());
//        System.out.println(counts);
        if (handType == Hand.HandType.ONE) {
            Count secondHighest = counts.get(1);
            if (secondHighest.count() - jokerCount == 2) {
                return new Bid(new Hand(notSortedCards, Hand.HandType.TWO, new int[]{highest.value(), secondHighest.value()}), bid);
            }
        } else if (handType == Hand.HandType.THREE) {
            Count secondHighest = counts.get(1);
            if (secondHighest.count() - jokerCount == 2) {
                return new Bid(new Hand(notSortedCards, Hand.HandType.HOUSE, new int[]{highest.value(), secondHighest.value()}), bid);
            }
        }
        return new Bid(new Hand(notSortedCards, handType, new int[]{highest.value()}), bid);
    }

    public record Count(int count, int value) {
    }

    public record Bid(Hand hand, int bidAmount) {
    }

    public record Hand(int[] cards, HandType handType, int[] value) {


        @Override
        public String toString() {
            return "Hand{" +
                    "cards=" + Arrays.toString(cards) +
                    ", handType=" + handType +
                    ", value=" + Arrays.toString(value) +
                    '}';
        }

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
                'T', 10,
                '9', 9,
                '8', 8,
                '7', 7,
                '6', 6,
                '5', 5,
                '4', 4,
                '3', 3,
                '2', 2
        ));

        public static final Map<Character, Integer> cardToValueWithJoker = new TreeMap<>();

        static {
            cardToValueWithJoker.putAll(cardToValue);
            cardToValue.putAll(Map.of(
                    'A', 14,
                    'K', 13,
                    'Q', 12,
                    'J', 11
            ));

            cardToValueWithJoker.putAll(Map.of(
                    'A', 14,
                    'K', 13,
                    'Q', 12,
                    'J', 1
            ));
        }

    }
}
