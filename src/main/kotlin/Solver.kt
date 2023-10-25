/**
 * Interface for a solver that can find solutions to a given [Matrix].
 */
interface Solver {
    /**
     * Find N solutions and stop looking.
     *
     * @param n the number of solutions to find
     * @return a list of [n] solutions as [Matrices][Matrix]
     */
    fun findNSolutions(n: Int): List<Matrix>

    /**
     * Find all solutions.
     *
     * @return a list of all solutions as [Matrices][Matrix]
     */
    fun findAllSolutions(): List<Matrix>
}