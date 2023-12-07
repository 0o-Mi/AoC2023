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
        caseOne();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(7, 1);
        dayUtils.startTimer();
        List<String> input = dayUtils.getListInput();
        List<Bid> bids = new ArrayList<>();
        for (String s : input) {
            String[] array = s.split(" ");
            String stringHand = array[0];
            String bidAmount = array[1];
            Bid bid = getBid(stringHand, Integer.parseInt(bidAmount));
            bids.add(bid);
        }
//        bids.sort(Comparator.comparingInt((Bid bid) -> bid.hand().handType().ordinal()).thenComparing((Bid bid) -> bid.hand().value()[0]));
        bids.forEach(System.out::println);
    }

    private static Bid getBid(String stringHand, int bid) {
        int[] cards = stringHand.chars().map(e -> Hand.cardToValue.get((char) e)).sorted().toArray();
        int count = 1;
        List<Count> counts = new ArrayList<>();
        System.out.println(Arrays.toString(cards));
        int previous = cards[0];
        for (int i = 1; i < cards.length; i++) {
            if (cards[i] == previous) {
                count++;
            } else {
                counts.add(new Count(count, cards[i - 1]));
                count = 1;
            }
            previous = cards[i];
        }
        counts.add(new Count(count, cards[cards.length - 1]));

                counts.sort(Comparator.comparing(Count::count).reversed());
        Count highest = counts.getFirst();
        Hand.HandType handType = Hand.HandType.integerToHandType.get(highest.count());
        System.out.println(counts);
        if (handType == Hand.HandType.ONE) {
            Count secondHighest = counts.get(1);
            if (secondHighest.count() == 2) {
                return new Bid(new Hand(cards, Hand.HandType.TWO, new int[]{highest.value(), secondHighest.value()}), bid);
            }
        } else if (handType == Hand.HandType.THREE) {
            Count secondHighest = counts.get(1);
            if (secondHighest.count() == 2) {
                return new Bid(new Hand(cards, Hand.HandType.HOUSE, new int[]{highest.value(), secondHighest.value()}), bid);
            }
        }
        return new Bid(new Hand(cards, handType, new int[]{highest.value()}), bid);
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

        static {
            cardToValue.putAll(Map.of(
                    'A', 14,
                    'K', 13,
                    'Q', 12,
                    'J', 11
            ));
        }

    }
}
