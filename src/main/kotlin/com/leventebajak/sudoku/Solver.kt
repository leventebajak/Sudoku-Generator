@file:JvmName("SolverUtils")

package com.leventebajak.sudoku

import com.leventebajak.dlx.DLX
import com.leventebajak.dlx.Matrix

/**
 * Sudoku solver using [DLX].
 */
object Solver {
    /**
     * Finds all solutions to the [board].
     *
     * @param board The [Board] to solve.
     * @return A [Sequence] of all solutions to the [board].
     */
    fun findAllSolutionsFor(board: Board): Sequence<Board> {
        return DLX(exactCoverMatrix, getClueRows(board)).findAllSolutions().map { it.toBoard() }
    }

    /**
     * Finds maximum [n] solutions to the [board].
     *
     * @param board The [Board] to solve.
     * @param n The maximum number of solutions to find.
     * @return A [List] of maximum [n] solutions to the [board].
     */
    fun findNSolutionsFor(board: Board, n: Int): List<Board> {
        require(n > 0) { "The number of solutions to find must be positive." }
        return DLX(exactCoverMatrix, getClueRows(board)).findNSolutions(n).toList().map { it.toBoard() }
    }

    /**
     * Gets the indices of the rows of the [exactCoverMatrix] corresponding to the
     * cells of the [board] are filled in, known as the clues.
     *
     * @param board The [Board] with the clues.
     * @return The indices of the rows of the [exactCoverMatrix] corresponding to the clues in the [board].
     */
    private fun getClueRows(board: Board): List<Int> {
        return mutableListOf<Int>().apply {
            repeat(9) { row ->
                repeat(9) { col ->
                    board[row, col].let { n ->
                        if (n != Board.EMPTY_CELL)
                            add(row * 81 + col * 9 + n - 1)
                    }
                }
            }
        }
    }

    /**
     * The [Exact Cover Matrix](https://www.stolaf.edu/people/hansonr/sudoku/exactcovermatrix.htm)
     * representation of the constraints of a Sudoku [Board].
     */
    private val exactCoverMatrix: Matrix by lazy {
        val columnCount = 324 // 9 rows * 9 columns * 4 constraints
        val rowCount = 729 // 9 rows * 9 columns * 9 numbers
        Matrix(List(rowCount) { MutableList(columnCount) { false } }.apply {
            // Fill in the matrix with the constraints
            repeat(9) { row ->
                repeat(9) { col ->
                    repeat(9) { n ->
                        with(this[row * 81 + col * 9 + n]) {
                            // Cell constraint
                            this[row * 9 + col] = true

                            // Row constraint
                            this[81 + row * 9 + n] = true

                            // Column constraint
                            this[162 + col * 9 + n] = true

                            // Box constraint
                            this[243 + (row / 3 * 3 + col / 3) * 9 + n] = true
                        }
                    }
                }
            }
        })
    }
}

/**
 * Finds all solutions for this [Board].
 *
 * @return A [Sequence] of all solutions for this [Board].
 */
fun Board.solutionSequence() = Solver.findAllSolutionsFor(this)