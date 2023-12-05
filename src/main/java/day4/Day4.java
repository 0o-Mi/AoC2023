package day4;

import day3.Day3;
import utils.DayUtils;
import utils.RegexPatterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.RegexPatterns.NUMBER;

public class Day4 {
//    private static final Pattern CARD_NUMBER = Pattern.compile("Card (\\d+):");

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partTwo() {
        DayUtils dayUtils = new DayUtils(4, 2);
        List<String> stringCards = dayUtils.getListInput();
        dayUtils.startTimer();
        List<Card> cards = getWinningCountList(stringCards);
        for (int cardNumber = 0; cardNumber < cards.size(); cardNumber++) {
            Card card = cards.get(cardNumber);
            for (int k = 0; k < card.cardCount; k++) {
                for (int j = 1; j <= card.getWinningCount(); j++) {
                    Card cardToIncrement = cards.get(cardNumber + j);
                    cardToIncrement.setCardCount(cardToIncrement.getCardCount()+1);
                }
            }
        }
        Integer sum = cards.stream().map(e -> e.cardCount).reduce(Integer::sum).orElseThrow();
        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }


    private static void partOne() {
        DayUtils dayUtils = new DayUtils(4, 1);
        List<String> stringCards = dayUtils.getListInput();
        dayUtils.startTimer();
        List<Card> cards = getWinningCountList(stringCards);
        int sum = cards.stream()
                .map(Card::getWinningCount)
                .map(Day4::getPoints)
                .reduce(Integer::sum).orElse(0);
        dayUtils.endTimer();
        dayUtils.printAnswer(sum);
    }

    private static int getPoints(int winningCount) {
        if (winningCount != 0) {
            return (int) Math.pow(2, winningCount - 1);
        }
        return 0;
    }

    private static List<Card> getWinningCountList(List<String> stringCards) {
        List<Card> cards = new ArrayList<>();
        for (String s : stringCards) {
            Matcher winningMatcher = NUMBER.matcher(s.substring(s.indexOf(":"), s.indexOf("|")));
            Matcher inputMatcher = NUMBER.matcher(s.substring(s.indexOf("|")));
            List<Integer> winningNumbers = new ArrayList<>();
            List<Integer> inputNumbers = new ArrayList<>();
            while (winningMatcher.find()) {
                winningNumbers.add(Integer.parseInt(winningMatcher.group()));
            }
            while (inputMatcher.find()) {
                inputNumbers.add(Integer.parseInt(inputMatcher.group()));
            }
            cards.add(new Card(getWinningCountList(winningNumbers, inputNumbers), 1));
        }
        return cards;
    }

    private static int getWinningCountList(List<Integer> winningNumbers, List<Integer> inputNumbers) {
        int winningCount = 0;
        for (Integer inputNumber : inputNumbers) {
            for (Integer winningNumber : winningNumbers) {
                if (winningNumber.equals(inputNumber)) {
                    winningCount++;
                }
            }
        }
        return winningCount;
    }

    public static final class Card {
        private final int winningCount;
        private int cardCount;

        public Card(int winningCount, int cardCount) {
            this.winningCount = winningCount;
            this.cardCount = cardCount;
        }

        public int getWinningCount() {
            return this.winningCount;
        }

        public int getCardCount() {
            return this.cardCount;
        }

        public void setCardCount(int cardCount) {
            this.cardCount = cardCount;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof Card other)) return false;
            if (this.getWinningCount() != other.getWinningCount()) return false;
            return this.getCardCount() == other.getCardCount();
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + this.getWinningCount();
            result = result * PRIME + this.getCardCount();
            return result;
        }

        public String toString() {
            return "Day4.Card(winningCount=" + this.getWinningCount() + ", cardCount=" + this.getCardCount() + ")";
        }
    }
}
