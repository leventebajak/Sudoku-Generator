import java.util.*

/**
 * A 9x9 Sudoku board with a unique solution and a set of clues.
 *
 * @property clues The board with the clues.
 * @property solution The solution to the board.
 */
class Sudoku {
    val clues: Array<IntArray>
    val solution: Array<IntArray>

    /**
     * The difficulty of a Sudoku board.
     * Determines how many clues are given.
     */
    enum class Difficulty { EASY, MEDIUM, HARD }

    /**
     * Creates a Sudoku board from a 9x9 array of numbers.
     *
     * @param board The board with the clues.
     * @throws IllegalArgumentException If the board is invalid or has zero or multiple solutions.
     */
    constructor(board: Array<IntArray>) {
        this.clues = board
        val solutions = getSolutions(board)
        if (solutions.size != 1)
            throw IllegalArgumentException("Sudoku board must have exactly one solution")
        solution = solutions[0]
    }

    /**
     * Creates a Sudoku board from a multiline string.
     *
     * @param multilineString The board with the clues.
     */
    constructor(multilineString: String) : this(multilineString.toBoard())

    /**
     * Creates a Sudoku board with a given difficulty.
     *
     * @param difficulty The difficulty of the board.
     * @throws IllegalArgumentException If the board is invalid or has zero or multiple solutions.
     */
    constructor(difficulty: Difficulty) {
        val clueCount = when (difficulty) {
            Difficulty.EASY -> 35..45
            Difficulty.MEDIUM -> 26..34
            Difficulty.HARD -> 22..25
        }.random()

        solution = getFilledBoard()
        clues = Array(9) { solution[it].copyOf() }

        var solver: Solver

        val remainingCells = mutableListOf<Pair<Int, Int>>()
        for (row in 0..8)
            for (col in 0..8)
                remainingCells.add(Pair(row, col))
        var removed = 0
        var tries = 0
        val maxTries = 25
        while (removed < 81 - clueCount) {
            val (row, col) = remainingCells.random()
            val n = clues[row][col]
            clues[row][col] = 0

            // Check if the board is still solvable
            solver = getSolver(clues)
            if (solver.findAllSolutions().size > 1) {
                clues[row][col] = n

                if (++tries == maxTries)
                    break
                continue
            }

            remainingCells.remove(Pair(row, col))
            removed++
            tries = 0
        }
    }

    companion object {
        /**
         * Prints a Sudoku board to the console.
         *
         * @throws IllegalArgumentException If the board is invalid.
         */
        fun Array<IntArray>.print() {
            if (this.size != 9)
                throw IllegalArgumentException("Sudoku board must be 9x9")
            println("┏━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━┓")
            for (row in 0..8) {
                if (this[row].size != 9)
                    throw IllegalArgumentException("Sudoku board must be 9x9")
                print("┃")
                for (col in 0..8) {
                    print(" ${this[row][col]} ")
                    if (col % 3 == 2)
                        print("┃")
                    else
                        print("│")
                }
                println()
                if (row == 8)
                    println("┗━━━━━━━━━━━┻━━━━━━━━━━━┻━━━━━━━━━━━┛")
                else if (row % 3 == 2)
                    println("┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫")
                else
                    println("┃───┼───┼───┃───┼───┼───┃───┼───┼───┃")
            }
        }

        /**
         * Converts a multiline string to a Sudoku board.
         *
         * @return A 9x9 array of numbers.
         */
        fun String.toBoard(): Array<IntArray> {
            val lines = this.trimIndent().lines()
            if (lines.size != 9)
                throw IllegalArgumentException("Sudoku board must be 9x9")
            return lines.map { line ->
                if (line.length != 9)
                    throw IllegalArgumentException("Sudoku board must be 9x9")
                line.trim().map {
                    if (it in '0'..'9') it - '0'
                    else throw IllegalArgumentException("Illegal character in Sudoku board: \"$it\"")
                }.toIntArray()
            }.toTypedArray()
        }

        /**
         * Converts the [Matrix] to a Sudoku board.
         *
         * @return A 9x9 array of numbers.
         */
        fun Matrix.toBoard(): Array<IntArray> {
            val board = Array(9) { IntArray(9) }
            for (bitSet in this.rows) {
                val cellConstraint = bitSet.nextSetBit(0)
                if (cellConstraint == -1 || cellConstraint >= 81)
                    throw IllegalArgumentException("Matrix does not represent a Sudoku board")

                val row = cellConstraint / 9
                val col = cellConstraint % 9
                if (board[row][col] != 0)
                    throw IllegalArgumentException("Multiple numbers in the same cell")

                val rowConstraint = bitSet.nextSetBit(81)
                if (rowConstraint == -1 || rowConstraint >= 162)
                    throw IllegalArgumentException("Matrix does not represent a Sudoku board")

                val number = rowConstraint - 81 - row * 9 + 1

                board[row][col] = number
            }
            return board
        }

        /**
         * Gets a solver for a given Sudoku board.
         *
         * @param board The board with the clues.
         * @return A [Solver] for the board.
         */
        private fun getSolver(board: Array<IntArray>): Solver = DLX(getMatrix(), getClueRows(board))

        /**
         * Gets the [Exact Cover Matrix](https://www.stolaf.edu/people/hansonr/sudoku/exactcovermatrix.htm)
         * representation of the constraints of a Sudoku board.
         *
         * @return A [Matrix] representing the constraints of a Sudoku board.
         */
        private fun getMatrix(): Matrix {
            val matrix = Matrix(324, MutableList(729) { BitSet(324) })

            // Fill in the matrix with every possible combination
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

                        // println("Row $i: ${matrix.rows[i]}")
                    }
                }
            }
            return matrix
        }

        /**
         * Gets the rows of the [Exact Cover Matrix](https://www.stolaf.edu/people/hansonr/sudoku/exactcovermatrix.htm)
         * that are filled in. These are the clues of the Sudoku board.
         *
         * @param board The board with the clues.
         * @return A list of the filled in rows.
         * @throws IllegalArgumentException If the board is invalid.
         * @see getMatrix
         */
        private fun getClueRows(board: Array<IntArray>): MutableList<Int> {
            val clueRows = mutableListOf<Int>()
            if (board.size != 9)
                throw IllegalArgumentException("Sudoku board must be 9x9")
            for (row in 0..8) {
                if (board[row].size != 9)
                    throw IllegalArgumentException("Sudoku board must be 9x9")
                for (col in 0..8) {
                    val n = board[row][col]
                    if (n == 0)
                        continue
                    if (n !in 1..9)
                        throw IllegalArgumentException("Illegal number in Sudoku board: \"$n\"")

                    clueRows.add(row * 81 + col * 9 + n - 1)
                }
            }
            return clueRows
        }

        /**
         * Gets a filled Sudoku board.
         *
         * @return A 9x9 array of numbers.
         * @throws Exception If the board could not be generated.
         */
        private fun getFilledBoard(): Array<IntArray> {
            val board = Array(9) { IntArray(9) }

            // Fill the diagonal cells
            for (i in 0..2) {
                val boxRow = i * 3
                val boxCol = i * 3
                val numbers = (1..9).shuffled()
                for (j in 0..2)
                    board[boxRow + j][boxCol + j] = numbers[j]
            }

            val solver = getSolver(board)
            val solution = solver.findFirstSolution() ?: throw Exception("Could not generate Sudoku board")

            return solution.toBoard()
        }

        /**
         * Gets all solutions to a given Sudoku board.
         *
         * @param board The board with the clues.
         * @return A list of all solutions to the board.
         */
        fun getSolutions(board: Array<IntArray>) = getSolver(board).findAllSolutions().map { it.toBoard() }
    }
}