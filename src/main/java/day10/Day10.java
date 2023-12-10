package day10;

import day10.Day10.Grid.GridItem;
import utils.DayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static day10.Day10.GridNavigator.NEXT_DIRECTION.DOWN;
import static day10.Day10.GridNavigator.NEXT_DIRECTION.LEFT;
import static day10.Day10.GridNavigator.NEXT_DIRECTION.RIGHT;
import static day10.Day10.GridNavigator.NEXT_DIRECTION.UP;

public class Day10 {
    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        DayUtils dayUtils = new DayUtils(10, 1);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        GridNavigator gridNavigator = navigateLoop(input);
        int steps = gridNavigator.visited.size() / 2;
        dayUtils.endTimer();
        dayUtils.printAnswer(steps);
    }

    private static GridNavigator navigateLoop(List<String> input) {
        Grid grid = new Grid(input);
        GridItem startingPosition = grid.findFirst('S').orElseThrow();
        GridNavigator gridNavigator = new GridNavigator(grid, startingPosition, LEFT); // found in input // LEFT
        while (!gridNavigator.findNext()) {
        }
        return gridNavigator;
    }

    private static void partTwo() {
        DayUtils dayUtils = new DayUtils(10, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        GridNavigator gridNavigator = navigateLoop(input);
        Map<Integer, List<Integer>> collect = gridNavigator.visited.stream().collect(Collectors.groupingBy(e -> e.y, Collectors.mapping(e -> e.x, Collectors.toList())));
        int tilesEnclosedSum = 0;
        for (var row : collect.values()) {
            for (int i = 0; i < row.size(); i += 2) { // += 2 to skip nonIn
                int inLeft = row.get(i);
                int inRight = row.get(i);
                tilesEnclosedSum += inRight - inLeft - 1;
            }
        }
        dayUtils.endTimer();
        dayUtils.printAnswer(tilesEnclosedSum);
    }

    public static class GridNavigator {
        private final Grid grid;
        private GridItem currentPosition;
        private NEXT_DIRECTION nextDirection;
        private final List<GridItem> visited;

        public GridNavigator(Grid grid, GridItem currentPosition, NEXT_DIRECTION nextDirection) {
            this.grid = grid;
            this.currentPosition = currentPosition;
            this.nextDirection = nextDirection;
            this.visited = new ArrayList<>();
        }

        public boolean findNext() {
            GridItem nextItem = grid.get(currentPosition.y + nextDirection.y, currentPosition.x + nextDirection.x).orElseThrow();
            currentPosition = nextItem;
            visited.add(nextItem);
            switch (nextItem.value) {
                case 'F' -> {
                    if (nextDirection == UP) {
                        nextDirection = RIGHT;
                    } else if (nextDirection == LEFT) {
                        nextDirection = DOWN;
                    } else {
                        throw new RuntimeException("F: " + nextDirection);
                    }
                }
                case 'L' -> {
                    if (nextDirection == DOWN) {
                        nextDirection = RIGHT;
                    } else if (nextDirection == LEFT) {
                        nextDirection = UP;
                    } else {
                        throw new RuntimeException("L: " + nextDirection);
                    }
                }
                case '7' -> {
                    if (nextDirection == UP) {
                        nextDirection = LEFT;
                    } else if (nextDirection == RIGHT) {
                        nextDirection = DOWN;
                    } else {
                        throw new RuntimeException("7: " + nextDirection);
                    }
                }
                case 'J' -> {
                    if (nextDirection == RIGHT) {
                        nextDirection = UP;
                    } else if (nextDirection == DOWN) {
                        nextDirection = LEFT;
                    } else {
                        throw new RuntimeException("J: " + nextDirection);
                    }
                }
                case '|' -> {
                    if (nextDirection == UP || nextDirection == DOWN) {
                    } else {
                        throw new RuntimeException("|: " + nextDirection);
                    }
                }
                case '-' -> {
                    if (nextDirection == LEFT || nextDirection == RIGHT) {
                    } else {
                        throw new RuntimeException("-: " + nextDirection);
                    }
                }
                case 'S' -> {
                    return true;
                }
            }
            System.out.println(currentPosition + " " + nextDirection);
            return false;
        }

        public enum NEXT_DIRECTION {
            UP(-1, 0),
            DOWN(1, 0),
            LEFT(0, -1),
            RIGHT(0, 1);

            private final int x;
            private final int y;

            NEXT_DIRECTION(int y, int x) {
                this.x = x;
                this.y = y;
            }
        }
    }


    public static class Grid {
        private final char[][] grid;
        private final int width;
        private final int height;

        public Grid(List<String> lines) {
            this.width = lines.getFirst().length();
            this.height = lines.size();
            this.grid = lines.stream()
                    .map(String::toCharArray)
                    .toArray(char[][]::new);
        }

        public Optional<GridItem> findFirst(char value) {
            for (int y = 0; y < grid.length; y++) {
                char[] chars = grid[y];
                for (int x = 0; x < chars.length; x++) {
                    char character = chars[x];
                    if (character == value) {
                        return Optional.of(new GridItem(y, x, character));
                    }
                }
            }
            return Optional.empty();
        }

        public List<GridItem> getNeighbours3x3(int y, int x) {
            List<GridItem> neighbours = new ArrayList<>();
            get(y + 1, x + 1).ifPresent(neighbours::add);
            get(y + 1, x    ).ifPresent(neighbours::add);
            get(y + 1, x - 1).ifPresent(neighbours::add);

            get(y    , x + 1).ifPresent(neighbours::add);
            get(y    , x    ).ifPresent(neighbours::add);
            get(y    , x - 1).ifPresent(neighbours::add);

            get(y - 1, x + 1).ifPresent(neighbours::add);
            get(y - 1, x    ).ifPresent(neighbours::add);
            get(y - 1, x - 1).ifPresent(neighbours::add);
            return neighbours;
        }

        public List<GridItem> getNeighboursStar(int y, int x) {
            List<GridItem> neighbours = new ArrayList<>();
            get(y + 1, x).ifPresent(neighbours::add);

            get(y, x + 1).ifPresent(neighbours::add);
            get(y, x - 1).ifPresent(neighbours::add);

            get(y - 1, x).ifPresent(neighbours::add);
            return neighbours;
        }

        public Optional<GridItem> get(int y, int x) {
            try {
                char c = grid[y][x];
                return Optional.of(new GridItem(y, x, c));
            } catch (IndexOutOfBoundsException e) {
                return Optional.empty();
            }
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public record GridItem(int y, int x, char value) {
        }
    }
}
