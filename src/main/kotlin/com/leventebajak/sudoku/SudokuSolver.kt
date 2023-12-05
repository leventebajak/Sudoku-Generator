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
     * @return A [Sequence] of all solutions to the [board].
     */
    fun findAllSolutionsFor(board: Board): Sequence<Board> {
        val dlx = DLX(exactCoverMatrix, getClueRows(board))
        return dlx.findAllSolutions().map { it.toBoard() }
    }

    /**
     * Finds maximum [n] solutions to the [board].
     *
     * @param board The [Board] to solve.
     * @param n The maximum number of solutions to find.
     * @return A [List] of maximum [n] solutions to the [board].
     */
    fun findNSolutionsFor(board: Board, n: Int): List<Board> {
        val dlx = DLX(exactCoverMatrix, getClueRows(board))
        val solutions = dlx.findNSolutions(n).toList()
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
        val solutions = dlx.findNSolutions(2).toList()
        require(solutions.size == 1) { "The board has no or multiple solutions" }
        return solutions[0].toBoard()
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
        Matrix(columnCount, MutableList(rowCount) { BitSet(columnCount) }).apply {
            // Fill in the matrix with the constraints
            repeat(9) { row ->
                repeat(9) { col ->
                    repeat(9) { n ->
                        with(rows[row * 81 + col * 9 + n]) {
                            // Cell constraint
                            set(row * 9 + col)

                            // Row constraint
                            set(81 + row * 9 + n)

                            // Column constraint
                            set(162 + col * 9 + n)

                            // Box constraint
                            set(243 + (row / 3 * 3 + col / 3) * 9 + n)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Finds all solutions for this [Board].
 *
 * @return A [Sequence] of all solutions for this [Board].
 */
fun Board.solve() = SudokuSolver.findAllSolutionsFor(this)