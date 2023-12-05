package com.leventebajak.sudoku

/**
 * A 9x9 Sudoku board with a list of solutions.
 *
 * @property clues The [Board] with the clues.
 * @property solutions The solutions to the [Board] with the [clues].
 */
class Sudoku(val clues: Board, val solutions: List<Board>) {
    init {
        require(clues.withIndex().all { (index, clue) ->
            clue == Board.EMPTY_CELL || solutions.all { it[index] == clue }
        }) { "The given clues do not match the solutions." }
    }
}