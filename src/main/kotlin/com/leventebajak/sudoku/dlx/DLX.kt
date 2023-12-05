package com.leventebajak.sudoku.dlx

import java.util.BitSet

/**
 * An implementation of Donald Knuth's [Algorithm X](https://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X),
 * using [Dancing Links](https://en.wikipedia.org/wiki/Dancing_Links).
 *
 * Based on the paper [Solving Sudoku efficiently with Dancing Links](https://www.kth.se/social/files/58861771f276547fe1dbf8d1/HLaestanderMHarrysson_dkand14.pdf)
 * by Hjalmar Laestander and Mattias Harrysson.
 *
 * @param matrix the [Matrix] representing the problem
 * @param clueRows the rows of the [Matrix] that must be included in the solution
 *
 * @property header the header of the internal structure (the root of the circular doubly-linked list)
 * @property columnHeaders the [Column] headers of the internal structure
 * @property clues the [Node]s that must be included in the solution
 */
open class DLX(matrix: Matrix, clueRows: List<Int> = listOf()) {
    private val header = Column(-1)
    private val columnHeaders = Array(matrix.columns) { Column(it) }
    private val clues = mutableListOf<Node>()

    /**
     * Initializing the internal structure.
     */
    init {
        for (columnHeader in columnHeaders) {
            columnHeader.left = header.left
            columnHeader.right = header
            columnHeader.column = header
            header.left.right = columnHeader
            header.left = columnHeader
            header.size++
        }
        val remainingClueRows = clueRows.toMutableList()
        for ((index, row) in matrix.rows.withIndex()) {
            var prev: Node? = null
            var j = row.nextSetBit(0)
            while (j != -1) {
                val node = Node()
                node.up = columnHeaders[j].up
                node.down = columnHeaders[j]
                node.column = columnHeaders[j]
                columnHeaders[j].size++
                columnHeaders[j].up.down = node
                columnHeaders[j].up = node
                if (prev !== null) {
                    node.left = prev
                    node.right = prev.right
                    prev.right.left = node
                    prev.right = node
                } else {
                    node.left = node
                    node.right = node
                }
                prev = node
                j = row.nextSetBit(j + 1)

                if (index in remainingClueRows) {
                    clues.add(node)
                    remainingClueRows.remove(index)
                }
            }
        }

        // Cover the columns satisfied by the clues
        for (node in clues) {
            node.column.cover()
            var i = node.right
            while (i !== node) {
                i.column.cover()
                i = i.right
            }
        }
    }

    /**
     * Starting the [recursive search][recursiveSearch] for solutions.
     *
     * @return a [Sequence] of [Solution]s
     */
    private fun search(): Sequence<Solution> = sequence { yieldAll(recursiveSearch(clues)) }

    /**
     * Searching for solutions recursively.
     * Should only be used by [search], otherwise the clues will not be covered.
     *
     * @param solution the current solution being built
     * @return a [Sequence] of [Solution]s
     * @see search
     */
    private fun recursiveSearch(solution: MutableList<Node>): Sequence<Solution> = sequence {
        if (header.right as Column === header) {
            yield(solution.toList())
            return@sequence
        }
        val column = chooseMinColumn()
        if (column.size == 0)
            return@sequence // No solution
        column.cover()
        var r = column.down
        while (r !== column) {
            solution.add(r)
            var j = r.right
            while (j !== r) {
                j.column.cover()
                j = j.right
            }
            yieldAll(recursiveSearch(solution))
            solution.removeAt(solution.lastIndex)
            j = r.left
            while (j !== r) {
                j.column.uncover()
                j = j.left
            }
            r = r.down
        }
        column.uncover()
    }


    /**
     * Finding the first [n] solutions.
     *
     * @param n the number of solutions to find
     * @return a [Sequence] of [Matrices][Matrix]
     */
    fun findNSolutions(n: Int) = findAllSolutions().take(n)

    /**
     * Finding all solutions.
     *
     * @return a [Sequence] of [Matrices][Matrix]
     */
    fun findAllSolutions() = search().map { it.toMatrix() }

    /**
     * Converting a [Solution] to a [Matrix].
     *
     * @return a [Matrix] representing the solution.
     */
    private fun Solution.toMatrix() = Matrix(columnHeaders.size, mutableListOf()).apply {
        this@toMatrix.forEach { node ->
            val row = BitSet()
            row[node.column.id] = true
            var j = node.right
            while (j !== node) {
                row[j.column.id] = true
                j = j.right
            }
            rows.add(row)
        }
    }

    /**
     * Choosing the [Column] with the minimum number of [Node]s to minimize the branching factor.
     *
     * @return the [Column] with the minimum number of [Node]s
     * @throws IllegalArgumentException if there are no columns left to choose
     */
    private fun chooseMinColumn(): Column {
        require(header.size > 0) { "No column left to choose" }
        var min = Int.MAX_VALUE
        var i = header.right as Column
        var column = i
        while (i !== header) {
            if (i.size < min) {
                min = i.size
                column = i
            }
            i = i.right as Column
        }
        return column
    }
}

/**
 * Alias for a [List] of [Node]s.
 */
private typealias Solution = List<Node>