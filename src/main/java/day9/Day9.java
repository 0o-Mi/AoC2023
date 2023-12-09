package day9;

import utils.DayUtils;

import java.util.List;

public class Day9 {
    public static void main(String[] args) {
        caseOne();
    }

    private static void caseOne() {
        DayUtils dayUtils = new DayUtils(9, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
    }
}
