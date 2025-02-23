package com.leventebajak.sudoku

/**
 * A Sudoku puzzle with clues and solutions.
 *
 * @property clues The [Board] with the clues and empty cells.
 * @property solution The [Board] with the solution.
 * @param difficulty The [Difficulty] of the puzzle, which determines the number of clues.
 */
class Puzzle(difficulty: Difficulty) {
    val clues: Board
    val solution: Board

    init {
        start@ while (true) {
            val solution = getFilledBoard()
            val clues = solution.clone()

            var clueCount = 81

            val remainingIndices = (0..<clueCount).toMutableSet()

            val triedIndices = mutableSetOf<Int>()

            while (clueCount > difficulty.clues) {
                // If there are no more clues to remove, start over with a new board.
                if (remainingIndices.isEmpty())
                    continue@start

                val index = remainingIndices.random().also { remainingIndices.remove(it) }
                val removedValue = clues[index]
                clues[index] = Board.EMPTY

                val solutions = Solver.solve(clues, 2).size
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

            this.clues = clues
            this.solution = solution
            break
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
    }.let { Solver.solve(it, 1).first() }
}