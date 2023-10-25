package sudoku

/**
 * Generates a Sudoku board with a given difficulty.
 */
object SudokuGenerator {
    /**
     * The difficulty of a Sudoku board.
     * Determines how many clues are given.
     */
    enum class Difficulty { EASY, MEDIUM, HARD }

    /**
     * Creates a Sudoku board with a given difficulty.
     *
     * @param difficulty The difficulty of the board.
     * @throws IllegalArgumentException If the board is invalid or has zero or multiple solutions.
     * @return A [Sudoku] with the given [difficulty].
     */
    fun generate(difficulty: Difficulty): Sudoku {
        val clueCount = when (difficulty) {
            Difficulty.EASY -> 35..45
            Difficulty.MEDIUM -> 26..34
            Difficulty.HARD -> 22..25
        }.random()

        val clues = getFilledBoard()

        val remainingCells = mutableListOf<Pair<Int, Int>>()
        for (row in 0..8)
            for (col in 0..8)
                remainingCells.add(Pair(row, col))
        var removed = 0
        var tries = 0
        val maxTries = 25
        while (removed < 81 - clueCount) {
            val (row, col) = remainingCells.random()
            val n = clues[row, col]
            clues[row, col] = 0

            // Check if the board is still solvable
            if (SudokuSolver.findAllSolutionsFor(clues).size > 1) {
                clues[row, col] = n

                if (++tries == maxTries)
                    break
                continue
            }

            remainingCells.remove(Pair(row, col))
            removed++
            tries = 0
        }

        return Sudoku(clues)
    }

    /**
     * Gets a filled Sudoku [Board].
     *
     * @return A 9x9 array of numbers.
     * @throws Exception If the board could not be generated.
     */
    private fun getFilledBoard(): Board {
        val board = Board()

        // Fill the diagonal cells
        for (i in 0..2) {
            val boxRow = i * 3
            val boxCol = i * 3
            val numbers = (1..9).shuffled()
            for (j in 0..2)
                board[boxRow + j, boxCol + j] = numbers[j]
        }

        val solutions = SudokuSolver.findNSolutionsFor(board, 1)

        if (solutions.size != 1)
            throw Exception("Could not generate Sudoku board!")

        return solutions[0]
    }
}