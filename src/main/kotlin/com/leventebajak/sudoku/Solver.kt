@file:JvmName("SolverUtils")

package com.leventebajak.sudoku

import com.leventebajak.dlx.DLX
import com.leventebajak.dlx.Matrix
import com.leventebajak.dlx.nextTrueIndex

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
    fun solve(board: Board): Sequence<Board> {
        return DLX.run(exactCoverMatrix, getClueRows(board)).map { it.toBoard() }
    }

    /**
     * Finds maximum [n] solutions to the [board].
     *
     * @param board The [Board] to solve.
     * @param n The maximum number of solutions to find.
     * @return A [List] of maximum [n] solutions to the [board].
     */
    fun solve(board: Board, n: Int): List<Board> {
        require(n > 0) { "The number of solutions to find must be positive." }
        return solve(board).take(n).toList()
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
                        if (n != Board.EMPTY)
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

    /**
     * Converts the [Matrix] to a sudoku [Board].
     *
     * @return The [Board] represented by the [Matrix].
     * @throws IllegalArgumentException If the matrix does not represent a Sudoku board.
     */
    private fun Matrix.toBoard() = Board().also { board ->
        forEach { matrixRow ->
            val cellConstraint = matrixRow.nextTrueIndex(0)
            require(cellConstraint != -1 && cellConstraint < 81) { "The matrix does not represent a Sudoku board" }

            val row = cellConstraint / 9
            val col = cellConstraint % 9
            require(board[row, col] == Board.EMPTY) { "Multiple numbers in the same cell" }

            val rowConstraint = matrixRow.nextTrueIndex(81)
            require(rowConstraint != -1 && rowConstraint < 162) { "The matrix does not represent a Sudoku board" }

            val number = rowConstraint - 81 - row * 9 + 1

            board[row, col] = number
        }
    }
}

/**
 * This extension function is purely for convenience. It calls the [Solver.solve] function.
 *
 * @return A [Sequence] of all solutions for this [Board].
 */
fun Board.getSolutions() = Solver.solve(this)