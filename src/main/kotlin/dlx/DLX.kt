package dlx

import Matrix
import Solver
import java.util.BitSet

/**
 * An implementation of Donald Knuth's [Algorithm X](https://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X) using
 * [Dancing Links](https://en.wikipedia.org/wiki/Dancing_Links).
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
open class DLX(matrix: Matrix, clueRows: MutableList<Int> = mutableListOf()) : Solver {
    private val header = Column(-1)
    private val columnHeaders = Array(matrix.columns) { Column(it) }
    private val clues = mutableListOf<Node>()

    /**
     * Initializing the internal structure.
     */
    init {
        for (i in 0..<matrix.columns) {
            columnHeaders[i].left = header.left
            columnHeaders[i].right = header
            columnHeaders[i].column = header
            header.left.right = columnHeaders[i]
            header.left = columnHeaders[i]
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
                if (prev != null) {
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
    }

    /**
     * Solving the problem using Algorithm X.
     *
     * @param stopAfter the maximum number of solutions to find before stopping
     * @return a list of solutions (each solution is a list of [Node]s)
     */
    private fun solve(stopAfter: Int): List<List<Node>> {
        // Cover the columns satisfied by the clues
        for (node in clues) {
            node.column!!.cover()
            var i = node.right
            while (i != node) {
                i.column!!.cover()
                i = i.right
            }
        }

        // Search for solutions
        val solutions = mutableListOf<List<Node>>()
        search(solutions, clues, stopAfter)

        // Uncover the columns satisfied by the clues
        for (node in clues) {
            node.column!!.uncover()
            var i = node.right
            while (i != node) {
                i.column!!.uncover()
                i = i.right
            }
        }

        return solutions
    }

    /**
     * Searching for solutions recursively.
     *
     * @param solutions a list of solutions (each solution is a list of [Node]s)
     * @param currentSolution the current solution being built
     * @param stopAfter the maximum number of solutions to find before stopping (use -1 for no limit)
     * @return whether a solution was found
     */
    private fun search(
        solutions: MutableList<List<Node>>,
        currentSolution: MutableList<Node>,
        stopAfter: Int
    ): Boolean {
        if (header.right as Column == header) {
            solutions.add(currentSolution.toList())
            return true
        }
        val column = chooseMinColumn()
        if (column.size == 0)
            return false
        column.cover()
        var r = column.down
        while (r != column) {
            currentSolution.add(r)
            var j = r.right
            while (j != r) {
                j.column!!.cover()
                j = j.right
            }
            val solved = search(solutions, currentSolution, stopAfter)
            if (solved && stopAfter == solutions.size)
                return true
            currentSolution.removeAt(currentSolution.lastIndex)
            j = r.left
            while (j != r) {
                j.column!!.uncover()
                j = j.left
            }
            r = r.down
        }
        column.uncover()
        return false
    }

    override fun findNSolutions(n: Int) = solve(n).map { solutionToMatrix(it) }

    override fun findAllSolutions() = findNSolutions(-1)

    /**
     * Converting a solution to a [Matrix].
     *
     * @param solution a list of [Node]s
     * @return a [Matrix] representing the [solution]
     */
    private fun solutionToMatrix(solution: List<Node>): Matrix {
        val rows = mutableListOf<BitSet>()
        for (node in solution) {
            val row = BitSet()
            row[node.column!!.id] = true
            var j = node.right
            while (j != node) {
                row[j.column!!.id] = true
                j = j.right
            }
            rows.add(row)
        }
        return Matrix(columnHeaders.size, rows)
    }

    /**
     * Choosing the [Column] with the minimum number of [Node]s to minimize the branching factor.
     *
     * @return the [Column] with the minimum number of [Node]s
     */
    private fun chooseMinColumn(): Column {
        if (header.size == 0)
            throw Exception("No column left to choose")
        var min = Int.MAX_VALUE
        var i = header.right as Column
        var column = i
        while (i != header) {
            if (i.size < min) {
                min = i.size
                column = i
            }
            i = i.right as Column
        }
        return column
    }

    override fun toString(): String {
        return "DLX(columns=${columnHeaders.size}, clues=${clues.size})"
    }
}