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
        GridNavigator.NEXT_DIRECTION startingDirection = grid.getStartingDirection(startingPosition);
        System.out.println(startingPosition + " " + startingDirection);
        GridNavigator gridNavigator = new GridNavigator(grid, startingPosition, startingDirection);
        while (!gridNavigator.findNext()) {
        }
        return gridNavigator;
    }

    private static void partTwo() {
        DayUtils dayUtils = new DayUtils(10, 2);
        List<String> input = dayUtils.getListInput();
        dayUtils.startTimer();
        GridNavigator gridNavigator = navigateLoop(input);
        Map<Integer, List<Integer>> visitedByRow = gridNavigator.visited.stream().collect(
                Collectors.groupingBy(e -> e.y, Collectors.filtering(e -> e.value != '-', Collectors.mapping(e -> e.x, Collectors.toList()))));
        visitedByRow.forEach((k,v) -> v.sort(Comparator.comparingInt(e -> e)));
        visitedByRow.forEach((k,v) -> System.out.println(k + " " + v));
        int tilesEnclosedSum = 0;
        for (var row : visitedByRow.values()) {
            for (int i = 0; i < row.size(); i += 2) { // += 2 to skip nonIn
                int inLeft = row.get(i);
                int inRight = row.get(i + 1);
                tilesEnclosedSum += inRight - inLeft - 1;
                System.out.println(tilesEnclosedSum + " " + (inRight + " " + inLeft));
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
            Arrays.stream(grid.grid).forEach(System.out::println);
        }

        public boolean findNext() {
            GridItem nextItem = grid.get(currentPosition.y + nextDirection.y, currentPosition.x + nextDirection.x).orElseThrow();
            currentPosition = nextItem;
            visited.add(nextItem);
            return getNextDirection(nextItem);
        }

        private boolean getNextDirection(GridItem nextItem) {
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

        public GridNavigator.NEXT_DIRECTION getStartingDirection(GridItem startingPosition) {
            List<Optional<GridItem>> startingNeighbours = getNeighboursStar(startingPosition.y, startingPosition.x);
            System.out.println(startingNeighbours);
            Optional<GridNavigator.NEXT_DIRECTION> UP = startingNeighbours.get(0).map(top -> {
                if (top.value == '|' || top.value == 'F' || top.value == '7') {
                    return GridNavigator.NEXT_DIRECTION.UP;
                }
                return null;
            });
            if (UP.isPresent()) return UP.get();

            Optional<GridNavigator.NEXT_DIRECTION> LEFT = startingNeighbours.get(1).map(left -> {
                if (left.value == '-' || left.value == 'L' || left.value == 'F') {
                    return GridNavigator.NEXT_DIRECTION.LEFT;
                }
                return null;
            });
            if (LEFT.isPresent()) return LEFT.get();

            Optional<GridNavigator.NEXT_DIRECTION> RIGHT = startingNeighbours.get(2).map(right -> {
                if (right.value == '-' || right.value == 'J' || right.value == '7') {
                    return GridNavigator.NEXT_DIRECTION.RIGHT;
                }
                return null;
            });
            if (RIGHT.isPresent()) return RIGHT.get();

            Optional<GridNavigator.NEXT_DIRECTION> DOWN = startingNeighbours.get(3).map(down -> {
                if (down.value == '|' || down.value == 'J' || down.value == 'L') {
                    return GridNavigator.NEXT_DIRECTION.DOWN;
                }
                return null;
            });
            if (DOWN.isPresent()) return DOWN.get();

            throw new RuntimeException("No starting direction found");
        }

        public List<GridItem> getNeighbours3x3(int y, int x) {
            List<GridItem> neighbours = new ArrayList<>();
            get(y + 1, x + 1).ifPresent(neighbours::add);
            get(y + 1, x).ifPresent(neighbours::add);
            get(y + 1, x - 1).ifPresent(neighbours::add);

            get(y, x + 1).ifPresent(neighbours::add);
            get(y, x).ifPresent(neighbours::add);
            get(y, x - 1).ifPresent(neighbours::add);

            get(y - 1, x + 1).ifPresent(neighbours::add);
            get(y - 1, x).ifPresent(neighbours::add);
            get(y - 1, x - 1).ifPresent(neighbours::add);
            return neighbours;
        }

        public List<Optional<GridItem>> getNeighboursStar(int y, int x) {
            List<Optional<GridItem>> neighbours = new ArrayList<>();
            neighbours.add(get(y + 1, x));

            neighbours.add(get(y, x + 1));
            neighbours.add(get(y, x - 1));

            neighbours.add(get(y - 1, x));
            return neighbours.reversed();
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
