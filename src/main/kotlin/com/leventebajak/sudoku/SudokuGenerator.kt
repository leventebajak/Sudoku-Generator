package com.leventebajak.sudoku

import java.lang.IllegalArgumentException

/**
 * Generates [Sudoku] boards with unique solutions.
 */
object SudokuGenerator {
    /**
     * The difficulty of a Sudoku board.
     * @property clues The number of clues given.
     */
    enum class Difficulty(val clues: Int) {
        EASY(45),
        MEDIUM(38),
        HARD(30),
        VERY_HARD(26)
    }

    /**
     * Creates a Sudoku board with a given [difficulty].
     *
     * @param difficulty The [Difficulty] of the board.
     * @return A [Sudoku] with the given [difficulty].
     */
    fun generate(difficulty: Difficulty): Sudoku {
        // The final number of clues is not guaranteed to be exact
        val clueCount = difficulty.clues

        val solution = getFilledBoard()
        val clues = solution.clone()

        val remainingCells = mutableListOf<Pair<Int, Int>>().apply {
            for (row in 0..8)
                for (col in 0..8)
                    add(Pair(row, col))
        }
        var removedCount = 0

        var tries = 0
        val maxTries = 40

        while (removedCount < 81 - clueCount) {
            val (row, col) = remainingCells.random()
            val removedValue = clues[row, col]
            clues[row, col] = Board.EMPTY_CELL

            try {
                SudokuSolver.findOnlySolution(clues)
            } catch (_: IllegalArgumentException) {
                // If there is no solution, put the value back
                clues[row, col] = removedValue

                // If we tried too many times, give up
                if (++tries == maxTries)
                    break
                else
                    continue
            }

            remainingCells.remove(Pair(row, col))
            removedCount++
            tries = 0
        }

        return Sudoku(clues, listOf(solution))
    }

    /**
     * Gets a filled Sudoku [Board].
     *
     * @return A 9x9 array of numbers.
     */
    private fun getFilledBoard() = Board().apply {
        // Fill the diagonal cells
        repeat(3) { box ->
            val numbers = (1..9).shuffled()
            repeat(3) { row ->
                this[box * 3 + row, box * 3 + row] = numbers[row]
            }
        }
    }.let { SudokuSolver.findNSolutionsFor(it, 1).first() }
}