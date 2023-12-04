package com.leventebajak.sudoku

/**
 * A 9x9 Sudoku board with a list of solutions.
 *
 * @property clues The [Board] with the clues.
 * @property solutions The solutions to the [Board] with the [clues].
 */
class Sudoku(val clues: Board, val solutions: List<Board>) {

    /**
     * Creates a [Sudoku] with the given [clues] and finds all solutions.
     *
     * @param clues The [Board] with the clues.
     */
    constructor(clues: Board) : this(clues, SudokuSolver.findAllSolutionsFor(clues))

    init {
        for (row in 0..8)
            for (col in 0..8)
                if (clues[row, col] != Board.EMPTY_CELL && solutions.any { it[row, col] != clues[row, col] })
                    throw IllegalArgumentException("The given clues do not match the solutions.")
    }
}