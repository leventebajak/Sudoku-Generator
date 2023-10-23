/**
 * Interface for a solver that can find solutions to a given [Matrix].
 */
interface Solver {
    /**
     * Find the first solution and stop looking.
     *
     * @return the first solution as a [Matrix], or `null` if no solution exists
     */
    fun findFirstSolution(): Matrix?

    /**
     * Find all solutions.
     *
     * @return a list of all solutions as [Matrices][Matrix]
     */
    fun findAllSolutions(): List<Matrix>
}