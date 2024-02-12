package com.leventebajak.sudoku

/**
 * A 9x9 Sudoku board with a list of solutions.
 *
 * @property clues The [Board] with the clues.
 * @property solutions The solutions to the [Board] with the [clues].
 */
data class Sudoku(val clues: Board, val solutions: List<Board>) {
    init {
        require(clues.withIndex().all { (index, clue) ->
            clue == Board.EMPTY_CELL || solutions.all { it[index] == clue }
        }) { "The given clues do not match the solutions." }
    }

    companion object {
        /**
         * Calls the [Generator] to generate a Sudoku board with the given [difficulty].
         *
         * @param difficulty The [Difficulty] of the generated Sudoku.
         * @return A [Sudoku] with the given [difficulty].
         */
        @JvmStatic
        fun generate(difficulty: Difficulty) = Generator.generate(difficulty)
    }
}