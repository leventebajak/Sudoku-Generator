import java.util.BitSet

/**
 * An implementation of Donald Knuth's [Algorithm X](https://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X) using
 * [Dancing Links](https://en.wikipedia.org/wiki/Dancing_Links).
 *
 * Based on the paper [Solving Sudoku efficiently with Dancing Links](https://www.kth.se/social/files/58861771f276547fe1dbf8d1/HLaestanderMHarrysson_dkand14.pdf)
 * by Hjalmar Laestander and Mattias Harrysson.
 *
 * @param matrix the [Matrix] representing the problem
 * @param clueRows the rows of the matrix that must be included in the solution
 *
 * @property header the header of the internal structure (the root of the circular doubly-linked list)
 * @property columnHeaders the [Column] headers of the internal structure
 * @property clues the [Node]s that must be included in the solution
 */
class DLX(matrix: Matrix, clueRows: MutableList<Int> = mutableListOf()) : Solver {
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
     * @param stopAfterFirstSolution whether to stop after finding the first solution
     * @return a list of solutions (each solution is a list of [Node]s)
     */
    private fun solve(stopAfterFirstSolution: Boolean): List<List<Node>> {
        // Cover the columns satisfied by the clues
        for (node in clues) {
            cover(node.column!!)
            var i = node.right
            while (i != node) {
                cover(i.column!!)
                i = i.right
            }
        }

        // Search for solutions
        val solutions = mutableListOf<List<Node>>()
        search(solutions, clues, stopAfterFirstSolution)

        // Uncover the columns satisfied by the clues
        for (node in clues) {
            uncover(node.column!!)
            var i = node.right
            while (i != node) {
                uncover(i.column!!)
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
     * @param stopAfterFirstSolution whether to stop after finding the first solution
     * @return whether a solution was found
     */
    private fun search(
        solutions: MutableList<List<Node>>,
        currentSolution: MutableList<Node>,
        stopAfterFirstSolution: Boolean
    ): Boolean {
        if (header.right as Column == header) {
            solutions.add(currentSolution.toList())
            return true
        }
        val column = chooseMinColumn()
        if (column.size == 0)
            return false
        cover(column)
        var r = column.down
        while (r != column) {
            currentSolution.add(r)
            var j = r.right
            while (j != r) {
                cover(j.column!!)
                j = j.right
            }
            val solved = search(solutions, currentSolution, stopAfterFirstSolution)
            if (solved && stopAfterFirstSolution)
                return true
            currentSolution.removeAt(currentSolution.lastIndex)
            j = r.left
            while (j != r) {
                uncover(j.column!!)
                j = j.left
            }
            r = r.down
        }
        uncover(column)
        return false
    }

    override fun findFirstSolution(): Matrix? {
        val solutions = solve(true)
        if (solutions.isEmpty())
            return null
        if (solutions.size > 1)
            throw Exception("More than one solution found")
        return solutionToMatrix(solutions[0])
    }

    override fun findAllSolutions() = solve(false).map { solutionToMatrix(it) }

    /**
     * Converting a solution to a [Matrix].
     *
     * @param solution a list of [Node]s
     * @return a [Matrix] representing the solution
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

    companion object {

        /**
         * Covering a column.
         *
         * @param column the [Column] to cover
         */
        fun cover(column: Column) {
            column.right.left = column.left
            column.left.right = column.right
            column.column!!.size--
            var i = column.down
            while (i != column) {
                var j = i.right
                while (j != i) {
                    j.down.up = j.up
                    j.up.down = j.down
                    j.column!!.size--
                    j = j.right
                }
                i = i.down
            }
        }

        /**
         * Uncovering a column.
         *
         * @param column the [Column] to uncover
         */
        fun uncover(column: Column) {
            var i = column.up
            while (i != column) {
                var j = i.left
                while (j != i) {
                    j.column!!.size++
                    j.down.up = j
                    j.up.down = j
                    j = j.left
                }
                i = i.up
            }
            column.right.left = column
            column.left.right = column
            column.column!!.size++
        }
    }

    override fun toString(): String {
        return "DLX(columns=${columnHeaders.size}, clues=${clues.size})"
    }
}