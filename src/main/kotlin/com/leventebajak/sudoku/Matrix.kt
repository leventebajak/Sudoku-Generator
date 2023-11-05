package com.leventebajak.sudoku

import java.util.BitSet

/**
 * A matrix of binary values.
 *
 * @property columns The number of columns in the matrix.
 * @property rows The rows of the matrix as a list of [BitSet]s.
 */
class Matrix {
    val rows: MutableList<BitSet>
    var columns: Int
        private set

    /**
     * Creates a [Matrix] with the given number of [columns] and a list of [BitSet]s.
     *
     * @param columns The number of columns in the matrix.
     * @param rows The rows of the matrix.
     */
    constructor(columns: Int, rows: List<BitSet>) {
        this.columns = columns
        this.rows = rows.toMutableList()
    }

    /**
     * Creates a [Matrix] by parsing a list of [binary strings][binaryStrings].
     *
     * @param binaryStrings A list of binary strings.
     * @see binaryStringToBitSet
     * @throws IllegalArgumentException if the binary strings are not all the same length
     */
    constructor(binaryStrings: List<String>) {
        columns = binaryStrings[0].length
        rows = MutableList(binaryStrings.size) {
            binaryStrings[it].trim()
            if (binaryStrings[it].length != columns)
                throw IllegalArgumentException("All rows must have the same length")
            binaryStringToBitSet(binaryStrings[it])
        }
    }

    /**
     * Creates a [Matrix] by parsing a [multiline binary string][binaryMultiline].
     * The string is trimmed and split into lines.
     *
     * @param binaryMultiline A multiline binary string.
     * @see binaryStringToBitSet
     * @throws IllegalArgumentException if the binary strings are not all the same length
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
        fun binaryStringToBitSet(binaryString: String): BitSet {
            val bitSet = BitSet(binaryString.length)
            for (i in binaryString.indices)
                if (binaryString[i] == '1')
                    bitSet.set(i)
                else if (binaryString[i] != '0')
                    throw IllegalArgumentException("Illegal character in binary string: ${binaryString[i]}")
            return bitSet
        }
    }

    /**
     * Prints the matrix to the console.
     */
    fun print() {
        for (row in rows) {
            for (i in 0..<columns)
                print(if (row[i]) "1" else "0")
            println()
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

    override fun hashCode(): Int {
        var result = columns
        result = 31 * result + rows.hashCode()
        return result
    }
}