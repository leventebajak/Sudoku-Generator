package com.leventebajak.sudoku.dlx

import java.util.BitSet

/**
 * A matrix of binary values.
 *
 * @property columns The number of columns in the matrix.
 * @property rows The rows of the matrix as a list of [BitSet]s.
 */
class Matrix(val columns: Int, val rows: MutableList<BitSet>) {

    /**
     * Creates a [Matrix] by parsing a list of [binary strings][binaryStrings].
     *
     * @param binaryStrings A list of binary strings.
     * @see binaryStringToBitSet
     * @throws IllegalArgumentException if the binary strings are not all the same length
     */
    constructor(binaryStrings: List<String>) : this(
        columns = binaryStrings[0].length,
        rows = MutableList(binaryStrings.size) {
            binaryStrings[it].trim().also { line ->
                if (line.length != binaryStrings[0].length)
                    throw IllegalArgumentException("All binary strings must be the same length")
            }
            binaryStringToBitSet(binaryStrings[it])
        }
    )

    /**
     * Creates a [Matrix] by parsing a [multiline binary string][binaryMultiline].
     * The string is trimmed and split into lines.
     *
     * @param binaryMultiline A multiline binary string.
     * @see binaryStringToBitSet
     */
    constructor(binaryMultiline: String) : this(binaryMultiline.trimIndent().lines())

    companion object {
        /**
         * Creates a [BitSet] from a [binary string][binaryString].
         *
         * @param binaryString A string of '0' and '1' characters.
         * @return A [BitSet] with the same bits as the [binary string][binaryString].
         * @throws IllegalArgumentException if the [binary string][binaryString] contains characters other than '0' and '1'
         */
        fun binaryStringToBitSet(binaryString: String) = BitSet(binaryString.length).apply {
            binaryString.forEachIndexed { index, char ->
                when {
                    char == '1' -> set(index)
                    char != '0' -> throw IllegalArgumentException("Binary string must contain only '0' and '1' characters")
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (columns != other.columns) return false
        if (rows != other.rows) return false

        return true
    }

    override fun hashCode() = 31 * columns + rows.hashCode()
}