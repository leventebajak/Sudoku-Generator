@file:JvmName("MatrixUtils")

package com.leventebajak.dlx

/**
 * Alias for a [List] of [Boolean]s.
 */
typealias Row = List<Boolean>

/**
 * A matrix of binary values.
 *
 * @property columns The number of columns in the matrix.
 * @property rows The number of [Row]s in the matrix.
 */
class Matrix(private val data: List<Row>) : Iterable<Row> by data {
    val columns = data[0].size
    val rows = data.size

    init {
        require(data.all { it.size == columns }) { "All rows must have the same length" }
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

/**
 * Converts a multiline [String] to a [Matrix], where every line represents a row.
 *
 * @return A [Matrix] of [Boolean]s.
 */
fun String.toMatrix() = Matrix(this.trimIndent().lines().map { line ->
    line.trim().map {
        when (it) {
            '0' -> false
            '1' -> true
            else -> throw IllegalArgumentException("Binary string must contain only '0' and '1' characters")
        }
    }
})

/**
 * Gets the index of the next true value in the [Row] starting from [fromIndex].
 * If there is no true value, returns -1.
 *
 * @param fromIndex The index to start searching from.
 * @return The index of the next true value or -1 if there is no true value.
 * @see java.util.BitSet.nextSetBit
 */
fun Row.nextTrueIndex(fromIndex: Int = 0): Int {
    if (fromIndex !in indices)
        return -1
    for (index in fromIndex..<size)
        if (this[index])
            return index
    return -1
}