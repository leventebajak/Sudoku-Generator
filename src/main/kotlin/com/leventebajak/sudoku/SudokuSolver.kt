package com.leventebajak.sudoku

import com.leventebajak.sudoku.dlx.DLX
import com.leventebajak.sudoku.dlx.Matrix
import java.util.BitSet

/**
 * Sudoku solver using [DLX].
 */
object SudokuSolver {
    /**
     * Finds all solutions to the [board].
     *
     * @param board The [Board] to solve.
     * @param limit The maximum number of solutions to find before stopping.
     * @return A list of all solutions to the [board].
     */
    fun findAllSolutionsFor(board: Board, limit: Int = 1_000): List<Board> {
        val solutions = findNSolutionsFor(board, limit)
        if (solutions.size == limit)
            println("$limit solutions found, stopping")
        return solutions
    }

    /**
     * Finds maximum [n] solutions to the [board].
     *
     * @param board The [Board] to solve.
     * @param n The maximum number of solutions to find.
     * @return A list of maximum [n] solutions to the [board].
     */
    fun findNSolutionsFor(board: Board, n: Int): List<Board> {
        val dlx = DLX(exactCoverMatrix, getClueRows(board))
        val solutions = dlx.findNSolutions(n)
        return solutions.map { it.toBoard() }
    }

    /**
     * Finds the only solution to the [board].
     *
     * @param board The [Board] to solve.
     * @return The only solution to the [board] if it exists.
     * @throws IllegalArgumentException If the [board] has no or multiple solutions.
     */
    fun findOnlySolution(board: Board): Board {
        val dlx = DLX(exactCoverMatrix, getClueRows(board))
        val solutions = dlx.findNSolutions(2)
        return if (solutions.size == 1) solutions[0].toBoard()
        else throw IllegalArgumentException("The board has no or multiple solutions")
    }

    /**
     * Gets the indices of the rows of the [exactCoverMatrix] corresponding to the
     * cells of the [board] are filled in, known as the clues.
     *
     * @param board The [Board] with the clues.
     * @return The indices of the rows of the [exactCoverMatrix] corresponding to the clues in the [board].
     */
    private fun getClueRows(board: Board): MutableList<Int> {
        val clueRows = mutableListOf<Int>()
        for (row in 0..8) for (col in 0..8) {
            val n = board[row, col]
            if (n == Board.EMPTY_CELL)
                continue
            clueRows.add(row * 81 + col * 9 + n - 1)
        }
        return clueRows
    }

    /**
     * The [Exact Cover Matrix](https://www.stolaf.edu/people/hansonr/sudoku/exactcovermatrix.htm)
     * representation of the constraints of a Sudoku [Board].
     */
    private val exactCoverMatrix: Matrix by lazy {
        val columns = 324 // 9 rows * 9 columns * 4 constraints
        val rows = 729 // 9 rows * 9 columns * 9 numbers
        val matrix = Matrix(columns, MutableList(rows) { BitSet(columns) })

        // Fill in the matrix with possible combinations
        for (row in 0..8) {
            for (col in 0..8) {
                for (n in 1..9) {
                    val i = row * 81 + col * 9 + n - 1

                    // Cell constraint
                    matrix.rows[i][row * 9 + col] = true

                    // Row constraint
                    matrix.rows[i][81 + row * 9 + n - 1] = true

                    // Column constraint
                    matrix.rows[i][162 + col * 9 + n - 1] = true

                    // Box constraint
                    matrix.rows[i][243 + (row / 3 * 3 + col / 3) * 9 + n - 1] = true
                }
            }
        }

        matrix
    }
}