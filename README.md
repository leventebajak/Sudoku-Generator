# Sudoku Generator

## Description

This is a sudoku solver and generator package written in Kotlin, that uses Donald Knuth's
[Algorithm X](https://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X) to solve the puzzles. The algorithm is implemented
using [Dancing Links](https://en.wikipedia.org/wiki/Dancing_Links), based on the paper [Solving Sudoku efficiently with Dancing Links](https://www.kth.se/social/files/58861771f276547fe1dbf8d1/HLaestanderMHarrysson_dkand14.pdf) 
by Hjalmar Laestander and Mattias Harrysson.

It can find **all** solutions to a given puzzle, or a number of desired solutions.
It can also generate new puzzles with unique solutions.

## Usage

The usage of this package is demonstrated with the following examples:

### Generating a puzzle

```kotlin
val difficulty = Difficulty.entries.random()
println("Difficulty: $difficulty")

val puzzle = Puzzle(difficulty)

println("Clues:")
print(puzzle.clues)

println("Solution:")
print(puzzle.solution)
```

### Solving a sudoku board

```kotlin
val clues = "004500009 720000050 000104300 003000000 490007000 000043208 002030006 500000902 008050040".toBoard()
// The string is filtered for digits and can be in one line or multiple lines.

val solutions = clues.getSolutions()

solutions.forEachIndexed { index: Int, solution: Board ->
    println("Solution ${index + 1}:")
    println(solution.toString())
}
```

**Output**:

```
Solution 1:
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
Solution 2:
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