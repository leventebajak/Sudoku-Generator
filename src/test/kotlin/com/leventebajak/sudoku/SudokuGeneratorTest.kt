package com.leventebajak.sudoku

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [SudokuGenerator] class.
 */
class SudokuGeneratorTest {
    @Test
    fun generate() {
        for (difficulty in SudokuGenerator.Difficulty.entries) {
            val sudoku = SudokuGenerator.generate(difficulty)

            assertEquals(1, sudoku.solutions.size)

            for (i in 0..8) {
                val remainingInRow = (1..9).toMutableSet()
                val remainingInCol = (1..9).toMutableSet()
                for (j in 0..8) {
                    assertTrue(remainingInRow.remove(sudoku.solutions[0][i, j]))
                    assertTrue(remainingInCol.remove(sudoku.solutions[0][j, i]))
                    assertTrue(
                        sudoku.clues[i, j] == Board.EMPTY_CELL || sudoku.clues[i, j] == sudoku.solutions[0][i, j]
                    )
                }
            }

            for (boxRow in 0..2) for (boxCol in 0..2) {
                val remainingInBox = (1..9).toMutableSet()
                for (r in 0..2) for (c in 0..2)
                    assertTrue(remainingInBox.remove(sudoku.solutions[0][boxRow * 3 + r, boxCol * 3 + c]))
            }
        }
    }
}