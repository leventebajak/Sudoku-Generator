package com.leventebajak.sudoku

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

/**
 * Tests for [Puzzle] generation.
 */
class PuzzleTest {
    @Test
    fun generate() {
        for (difficulty in Difficulty.entries) {
            val puzzle = Puzzle(difficulty)

            for (i in 0..8) {
                val remainingInRow = (1..9).toMutableSet()
                val remainingInCol = (1..9).toMutableSet()
                for (j in 0..8) {
                    assertTrue(remainingInRow.remove(puzzle.solution[i, j]))
                    assertTrue(remainingInCol.remove(puzzle.solution[j, i]))
                    assertTrue(
                        puzzle.clues[i, j] == Board.EMPTY || puzzle.clues[i, j] == puzzle.solution[i, j]
                    )
                }
            }

            for (boxRow in 0..2) for (boxCol in 0..2) {
                val remainingInBox = (1..9).toMutableSet()
                for (r in 0..2) for (c in 0..2)
                    assertTrue(remainingInBox.remove(puzzle.solution[boxRow * 3 + r, boxCol * 3 + c]))
            }
        }
    }

    @Test
    fun generate100() {
        measureTime {
            repeat(100) {
                Puzzle(Difficulty.entries.random())
            }
        }.also {
            assertTrue("Generation took too long: $it") { it < 10.seconds }
        }
    }
}