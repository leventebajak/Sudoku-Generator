package com.leventebajak.sudoku

/**
 * Generates a [Sudoku] board with a unique solution.
 */
object SudokuGenerator {
    /**
     * Determines how many clues are given.
     */
    enum class Difficulty { EASY, MEDIUM, HARD, VERY_HARD }

    /**
     * Creates a Sudoku board with a given [difficulty].
     *
     * @param difficulty The [Difficulty] of the board.
     * @return A [Sudoku] with the given [difficulty].
     */
    fun generate(difficulty: Difficulty): Sudoku {
        // The final number of clues is not guaranteed to be exact
        val clueCount = when (difficulty) {
            Difficulty.EASY -> 45
            Difficulty.MEDIUM -> 38
            Difficulty.HARD -> 30
            Difficulty.VERY_HARD -> 26
        }

        val clues = getFilledBoard()

        val remainingCells = mutableListOf<Pair<Int, Int>>()
        for (row in 0..8)
            for (col in 0..8)
                remainingCells.add(Pair(row, col))
        var removedCount = 0

        var tries = 0
        val maxTries = 40

        var solution: Board? = null

        while (removedCount < 81 - clueCount) {
            val (row, col) = remainingCells.random()
            val removedValue = clues[row, col]
            clues[row, col] = Board.EMPTY_CELL

            solution = SudokuSolver.findUniqueSolutionFor(clues)

            // If there is no unique solution, put the value back
            if (solution == null) {
                clues[row, col] = removedValue

                // If we tried too many times, give up
                if (++tries == maxTries) {
                    solution = SudokuSolver.findUniqueSolutionFor(clues)
                    break
                }

                continue
            }

            remainingCells.remove(Pair(row, col))
            removedCount++
            tries = 0
        }

        return Sudoku(clues, listOf(solution!!))
    }

    /**
     * Gets a filled Sudoku [Board].
     *
     * @return A 9x9 array of numbers.
     */
    private fun getFilledBoard(): Board {
        val board = Board()

        // Fill the diagonal cells
        for (i in 0..2) {
            val boxRow = i * 3
            val boxCol = i * 3
            val numbers = (1..9).shuffled()
            for (j in 0..2)
                board[boxRow + j, boxCol + j] = numbers[j]
        }

        val solutions = SudokuSolver.findNSolutionsFor(board, 1)

        return solutions[0]
    }
}