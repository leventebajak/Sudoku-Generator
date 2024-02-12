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
        start@ while (true) {
            val solution = getFilledBoard()
            val clues = solution.clone()

            var clueCount = 81

            val remainingIndices = (0..<81).toMutableSet()

            val triedIndices = mutableSetOf<Int>()

            while (clueCount > difficulty.clues) {
                // If there are no more clues to remove, start over with a new board.
                if (remainingIndices.isEmpty())
                    continue@start

                val index = remainingIndices.random().also { remainingIndices.remove(it) }
                val removedValue = clues[index]
                clues[index] = Board.EMPTY_CELL

                val solutions = Solver.findNSolutionsFor(clues, 2).size
                if (solutions != 1) {
                    // If the number of solutions is no longer 1, put the value back.
                    clues[index] = removedValue
                    triedIndices.add(index)
                    continue
                }

                remainingIndices.addAll(triedIndices)
                triedIndices.clear()
                clueCount--
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