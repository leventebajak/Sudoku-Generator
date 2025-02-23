@file:JvmName("BoardUtils")

package com.leventebajak.sudoku

/**
 * A 9x9 Sudoku board.
 * @property cells The cells of the board in a 1D array.
 *
 * @constructor Creates a new Sudoku board with the given cells.
 * @param cells The cells of the board. If null, the board is initialized with empty cells.
 * @throws IllegalArgumentException If the board is not 9x9 or contains invalid values.
 * @see String.toBoard
 */
class Board(cells: IntArray? = null) : Iterable<Int>, Cloneable {
    private val cells: IntArray

    companion object {
        /**
         * The value of an empty cell.
         */
        const val EMPTY: Int = 0

        /**
         * The set of valid numbers.
         */
        @JvmStatic
        val NUMBERS = (1..9).toSet() + EMPTY

        /**
         * The character representing an empty cell.
         */
        const val EMPTY_CHAR: Char = '0'
    }

    init {
        if (cells == null) {
            this.cells = IntArray(81) { EMPTY }
        } else {
            require(cells.size == 81) { "The Sudoku board must be 9x9" }
            require(cells.all { it in NUMBERS }) { "The Sudoku board must contain only numbers in $NUMBERS" }
            this.cells = cells
        }
    }

    /**
     * Converts a multiline string to a sudoku [Board].
     * The string is filtered for digits and can be in one line or multiple lines.
     *
     * @param cells The string representing the board.
     * @return The [Board] represented by the string.
     * @see [Board.EMPTY_CHAR]
     */
    constructor(cells: String) : this(
        cells.filter { it.isDigit() || it == EMPTY_CHAR }
            .map { if (it == EMPTY_CHAR) EMPTY else it.toString().toInt() }
            .toIntArray()
    )

    /**
     * Indexing logic for a 9x9 [Board].
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The index of the cell at the given coordinates.
     */
    private fun index(row: Int, col: Int) = row * 9 + col

    /**
     * Gets the value of the cell at the given index.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The value of the cell at the given [row] and [column][col].
     */
    operator fun get(row: Int, col: Int): Int = get(index(row, col))

    /**
     * Sets the value of the cell at the given [row] and [column][col].
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param value The value to set the cell to.
     */
    operator fun set(row: Int, col: Int, value: Int) = set(index(row, col), value)

    /**
     * Gets the value of the cell at the given index.
     *
     * @param index The index of the cell.
     * @return The value of the cell at the given [index].
     */
    operator fun get(index: Int) = cells[index]

    /**
     * Sets the value of the cell at the given [index].
     *
     * @param index The index of the cell.
     * @param value The value to set the cell to.
     * @throws IllegalArgumentException If the value is not in [NUMBERS].
     */
    operator fun set(index: Int, value: Int) {
        require(value in NUMBERS) { "Cell value must be in $NUMBERS" }
        cells[index] = value
    }

    /**
     * Creates a deep copy of the board.
     *
     * @return A copy of the board.
     */
    public override fun clone() = Board(cells.clone())

    /**
     * Returns an iterator over the [cells] of the board.
     *
     * @return An iterator over the cells of the board.
     */
    override fun iterator() = cells.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return cells.contentEquals((other as Board).cells)
    }

    override fun hashCode() = cells.contentHashCode()

    override fun toString(): String {
        val builder = StringBuilder("┏━━━━━━━━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━┓\n")
        repeat(9) { row ->
            builder.append("┃")
            repeat(9) { col ->
                builder.append(" ${this[row, col].let { if (it == EMPTY) " " else it }} ${if (col % 3 == 2) "┃" else "│"}")
            }
            builder.append("\n")
            builder.append(
                when {
                    row == 8 -> "┗━━━━━━━━━━━┻━━━━━━━━━━━┻━━━━━━━━━━━┛"
                    row % 3 == 2 -> "┣━━━━━━━━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━┫\n"
                    else -> "┃───┼───┼───┃───┼───┼───┃───┼───┼───┃\n"
                }
            )
        }
        return builder.toString()
    }
}

/**
 * This extension function is purely for convenience. It calls the secondary constructor of [Board].
 *
 * @return The [Board] represented by the string.
 */
fun String.toBoard() = Board(this)

/**
 * Same as [String.toBoard], but replaces every occurrence of [empty] with [Board.EMPTY_CHAR].
 *
 * @param empty The character representing an empty cell.
 * @return The [Board] represented by the string.
 */
fun String.toBoard(empty: Char) = Board(this.replace(empty, Board.EMPTY_CHAR))