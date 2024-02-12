package com.leventebajak.sudoku

/**
 * The difficulty of a Sudoku board.
 * @property clues The number of clues given.
 */
enum class Difficulty(val clues: Int) {
    EASY(45),
    MEDIUM(38),
    HARD(32),
    VERY_HARD(27);

    override fun toString() = name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() }
}