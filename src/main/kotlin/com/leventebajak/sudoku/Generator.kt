package com.leventebajak.sudoku

/**
 * Generates [Sudoku] boards with unique solutions.
 */
object Generator {
    /**
     * Creates a Sudoku board with a given [difficulty].
     *
     * @param difficulty The [Difficulty] of the board.
     * @return A [Sudoku] with the given [difficulty].
     */
    fun generate(difficulty: Difficulty): Sudoku {
        loop@ while (true) {
            val solution = getFilledBoard()
            val clues = solution.clone()

            val remainingCells = mutableListOf<Pair<Int, Int>>().apply {
                for (row in 0..8)
                    for (col in 0..8)
                        add(Pair(row, col))
            }

            var leftToRemove = 81 - difficulty.clues

            val tries = mutableSetOf<Pair<Int, Int>>()

            while (leftToRemove > 0) {
                if (remainingCells.isEmpty())
                    // There are no more clues to remove, so start over with a new board.
                    continue@loop

                val (row, col) = remainingCells.removeAt((0..<remainingCells.size).random())
                val removedValue = clues[row, col]
                clues[row, col] = Board.EMPTY_CELL

                val solutions = Solver.findNSolutionsFor(clues, 2).size
                if (solutions != 1) {
                    // If there is no unique solution, revert the removal.
                    clues[row, col] = removedValue
                    tries.add(Pair(row, col))
                    continue
                }

                remainingCells.addAll(tries)
                tries.clear()
                leftToRemove--
            }

            return Sudoku(clues, listOf(solution))
        }
    }

    /**
     * Gets a filled Sudoku [Board].
     *
     * @return A 9x9 array of numbers.
     */
    private fun getFilledBoard() = Board().apply {
        // Filling the diagonal cells
        repeat(3) { box ->
            val numbers = (1..9).shuffled()
            repeat(3) { row ->
                this[box * 3 + row, box * 3 + row] = numbers[row]
            }
        }
    }.let { Solver.findNSolutionsFor(it, 1).first() }
}