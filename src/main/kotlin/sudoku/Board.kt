package sudoku

import Matrix

/**
 * A 9x9 [Sudoku] board.
 *
 * @property cells The cells of the board.
 * @throws IllegalArgumentException If the board is not 9x9 or contains invalid values.
 * @see String.toBoard
 * @see Matrix.toBoard
 */
data class Board(private val cells: Array<IntArray> = Array(9) { IntArray(9) }) {
    /**
     * Checks if the board is valid.
     */
    init {
        if (cells.size != 9)
            throw IllegalArgumentException("Sudoku board must be 9x9")
        for (row in 0..8) {
            if (cells[row].size != 9)
                throw IllegalArgumentException("Sudoku board must be 9x9")
            for (col in 0..8) {
                if (cells[row][col] !in 0..9)
                    throw IllegalArgumentException("Cell value must be between 0 and 9")
            }
        }
    }

    /**
     * Prints a [Sudoku] board to the console.
     *
     * @throws IllegalArgumentException If the board is invalid.
     */
    fun print() {
        if (cells.size != 9)
            throw IllegalArgumentException("Sudoku board must be 9x9")
        println("┏━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━┓")
        for (row in 0..8) {
            if (cells[row].size != 9)
                throw IllegalArgumentException("Sudoku board must be 9x9")
            print("┃")
            for (col in 0..8) {
                print(" ${cells[row][col]} ")
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
     * Gets the value of the cell at the given index.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The value of the cell at the given [row] and [column][col].
     */
    operator fun get(row: Int, col: Int): Int = cells[row][col]

    /**
     * Sets the value of the cell at the given [row] and [column][col].
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param value The value to set the cell to.
     * @throws IllegalArgumentException If the value is not between 0 and 9.
     */
    operator fun set(row: Int, col: Int, value: Int) {
        if (value !in 0..9)
            throw IllegalArgumentException("Cell value must be between 0 and 9")
        cells[row][col] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        return cells.contentDeepEquals(other.cells)
    }

    override fun hashCode(): Int {
        return cells.contentDeepHashCode()
    }
}

/**
 * Converts a multiline string to a [Sudoku] board.
 *
 * @return A 9x9 array of numbers.
 */
fun String.toBoard(): Board {
    val lines = this.trimIndent().lines()
    if (lines.size != 9)
        throw IllegalArgumentException("Sudoku board must be 9x9")
    return Board(lines.map { line ->
        if (line.length != 9)
            throw IllegalArgumentException("Sudoku board must be 9x9")
        line.trim().map {
            if (it in '0'..'9') it - '0'
            else throw IllegalArgumentException("Illegal character in Sudoku board: \"$it\"")
        }.toIntArray()
    }.toTypedArray())
}

/**
 * Converts the [Matrix] to a [Sudoku] board.
 *
 * @return A 9x9 array of numbers.
 */
fun Matrix.toBoard(): Board {
    val board = Board()
    for (bitSet in this.rows) {
        val cellConstraint = bitSet.nextSetBit(0)
        if (cellConstraint == -1 || cellConstraint >= 81)
            throw IllegalArgumentException("Matrix does not represent a Sudoku board")

        val row = cellConstraint / 9
        val col = cellConstraint % 9
        if (board[row, col] != 0)
            throw IllegalArgumentException("Multiple numbers in the same cell")

        val rowConstraint = bitSet.nextSetBit(81)
        if (rowConstraint == -1 || rowConstraint >= 162)
            throw IllegalArgumentException("Matrix does not represent a Sudoku board")

        val number = rowConstraint - 81 - row * 9 + 1

        board[row, col] = number
    }
    return board
}