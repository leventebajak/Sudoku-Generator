# Sudoku - Console

## Description

This is a sudoku solver and generator written in Kotlin, that uses Donald Knuth's
[Algorithm X](https://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X) to solve the puzzles. The algorithm is implemented
using [Dancing Links](https://en.wikipedia.org/wiki/Dancing_Links).

It can find all solutions to a given puzzle, or just the first one. It can also generate new puzzles with unique
solutions.

## Usage

### Generating a puzzle

```kotlin
fun main() {
    val sudoku = SudokuGenerator.generate(SudokuGenerator.Difficulty.EASY)
    println("Clues:")
    sudoku.clues.print()
    for ((index, solution) in sudoku.solutions.withIndex()) {
        println("Solution ${index + 1} of ${sudoku.solutions.size}:")
        solution.print()
    }
}
```

**Output**:

```
Clues:
┏━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━┓
┃ 8 │ 0 │ 0 ┃ 7 │ 9 │ 0 ┃ 0 │ 5 │ 0 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 0 │ 7 │ 6 ┃ 5 │ 0 │ 1 ┃ 0 │ 0 │ 0 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 0 │ 1 │ 0 ┃ 8 │ 4 │ 3 ┃ 7 │ 6 │ 0 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 0 │ 2 │ 7 ┃ 6 │ 8 │ 0 ┃ 0 │ 4 │ 5 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 0 │ 5 │ 0 ┃ 0 │ 0 │ 2 ┃ 6 │ 9 │ 8 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 6 │ 9 │ 0 ┃ 3 │ 0 │ 4 ┃ 0 │ 0 │ 7 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 9 │ 8 │ 0 ┃ 0 │ 6 │ 0 ┃ 5 │ 0 │ 1 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 2 │ 0 │ 0 ┃ 9 │ 1 │ 8 ┃ 0 │ 0 │ 0 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 0 │ 6 │ 0 ┃ 0 │ 0 │ 5 ┃ 8 │ 2 │ 9 ┃
┗━━━━━━━━━━━┻━━━━━━━━━━━┻━━━━━━━━━━━┛
Solution 1 of 1:
┏━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━┓
┃ 8 │ 4 │ 2 ┃ 7 │ 9 │ 6 ┃ 1 │ 5 │ 3 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 3 │ 7 │ 6 ┃ 5 │ 2 │ 1 ┃ 9 │ 8 │ 4 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 5 │ 1 │ 9 ┃ 8 │ 4 │ 3 ┃ 7 │ 6 │ 2 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 1 │ 2 │ 7 ┃ 6 │ 8 │ 9 ┃ 3 │ 4 │ 5 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 4 │ 5 │ 3 ┃ 1 │ 7 │ 2 ┃ 6 │ 9 │ 8 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 6 │ 9 │ 8 ┃ 3 │ 5 │ 4 ┃ 2 │ 1 │ 7 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 9 │ 8 │ 4 ┃ 2 │ 6 │ 7 ┃ 5 │ 3 │ 1 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 2 │ 3 │ 5 ┃ 9 │ 1 │ 8 ┃ 4 │ 7 │ 6 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 7 │ 6 │ 1 ┃ 4 │ 3 │ 5 ┃ 8 │ 2 │ 9 ┃
┗━━━━━━━━━━━┻━━━━━━━━━━━┻━━━━━━━━━━━┛
```

### Solving a puzzle

```kotlin
fun main() {
    val sudoku = Sudoku(
        """
            004500009
            720000050
            000104300
            003000000
            490007000
            000043208
            002030006
            500000902
            008050040
        """.toBoard()
    )
    for ((index, solution) in sudoku.solutions.withIndex()) {
        println("Solution ${index + 1} of ${sudoku.solutions.size}:")
        solution.print()
    }
}
```

**Output**:

```
Solution 1 of 2:
┏━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━┓
┃ 3 │ 1 │ 4 ┃ 5 │ 7 │ 2 ┃ 8 │ 6 │ 9 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 7 │ 2 │ 9 ┃ 3 │ 6 │ 8 ┃ 4 │ 5 │ 1 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 8 │ 6 │ 5 ┃ 1 │ 9 │ 4 ┃ 3 │ 2 │ 7 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 2 │ 8 │ 3 ┃ 6 │ 1 │ 5 ┃ 7 │ 9 │ 4 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 4 │ 9 │ 1 ┃ 8 │ 2 │ 7 ┃ 6 │ 3 │ 5 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 6 │ 5 │ 7 ┃ 9 │ 4 │ 3 ┃ 2 │ 1 │ 8 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 1 │ 4 │ 2 ┃ 7 │ 3 │ 9 ┃ 5 │ 8 │ 6 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 5 │ 3 │ 6 ┃ 4 │ 8 │ 1 ┃ 9 │ 7 │ 2 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 9 │ 7 │ 8 ┃ 2 │ 5 │ 6 ┃ 1 │ 4 │ 3 ┃
┗━━━━━━━━━━━┻━━━━━━━━━━━┻━━━━━━━━━━━┛
Solution 2 of 2:
┏━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━┓
┃ 3 │ 1 │ 4 ┃ 5 │ 7 │ 6 ┃ 8 │ 2 │ 9 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 7 │ 2 │ 6 ┃ 3 │ 9 │ 8 ┃ 4 │ 5 │ 1 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 8 │ 5 │ 9 ┃ 1 │ 2 │ 4 ┃ 3 │ 6 │ 7 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 2 │ 8 │ 3 ┃ 6 │ 1 │ 5 ┃ 7 │ 9 │ 4 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 4 │ 9 │ 1 ┃ 2 │ 8 │ 7 ┃ 6 │ 3 │ 5 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 6 │ 7 │ 5 ┃ 9 │ 4 │ 3 ┃ 2 │ 1 │ 8 ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫
┃ 1 │ 4 │ 2 ┃ 8 │ 3 │ 9 ┃ 5 │ 7 │ 6 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 5 │ 3 │ 7 ┃ 4 │ 6 │ 1 ┃ 9 │ 8 │ 2 ┃
┃───┼───┼───┃───┼───┼───┃───┼───┼───┃
┃ 9 │ 6 │ 8 ┃ 7 │ 5 │ 2 ┃ 1 │ 4 │ 3 ┃
┗━━━━━━━━━━━┻━━━━━━━━━━━┻━━━━━━━━━━━┛
```