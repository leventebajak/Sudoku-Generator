package com.leventebajak.sudoku

/**
 * A 9x9 Sudoku board with a list of solutions.
 *
 * @property clues The [Board] with the clues.
 * @property solutions The solutions to the [Board] with the [clues].
 */
class Sudoku {
    val solutions: List<Board>
    val clues: Board

    constructor(clues: Board, solutions: List<Board>) {
        this.clues = clues
        this.solutions = solutions

        for (row in 0..8)
            for (col in 0..8)
                if (clues[row, col] != Board.EMPTY_CELL && solutions.any { it[row, col] != clues[row, col] })
                    throw IllegalArgumentException("The given clues do not match the solutions.")
    }

    constructor(clues: Board) : this(clues, SudokuSolver.findAllSolutionsFor(clues))
}