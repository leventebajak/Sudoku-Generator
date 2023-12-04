package com.leventebajak.sudoku

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * Test for the [SudokuSolver] class.
 */
class SudokuSolverTest {
    @Test
    fun singleSolution() {
        val board = """
                000260701
                680070090
                190004500
                820100040
                004602900
                050003028
                009300074
                040050036
                703018000
            """.toBoard()

        val solutions = SudokuSolver.findAllSolutionsFor(board)

        assertEquals(1, solutions.size)

        val expectedSolution = """
                435269781
                682571493
                197834562
                826195347
                374682915
                951743628
                519326874
                248957136
                763418259
            """.toBoard()

        for (row in 0..8)
            for (col in 0..8)
                assertEquals(expectedSolution[row, col], solutions[0][row, col])
    }

    @Test
    fun multipleSolutions() {
        val board = """
                004500009
                720000050
                000104300
                003000000
                490007000
                000043208
                002030006
                500000902
                008050040
            """.toBoard()

        val solutions = SudokuSolver.findAllSolutionsFor(board)

        assertEquals(2, solutions.size)

        val expectedSolutions = arrayOf(
            """
                314572869
                729368451
                865194327
                283615794
                491827635
                657943218
                142739586
                536481972
                978256143
            """.toBoard(),
            """
                314576829
                726398451
                859124367
                283615794
                491287635
                675943218
                142839576
                537461982
                968752143
            """.toBoard()
        )

        var match = true
        var matchOpposite = true
        for (row in 0..8)
            for (col in 0..8) {
                match = match
                        && expectedSolutions[0][row, col] == solutions[0][row, col]
                        && expectedSolutions[1][row, col] == solutions[1][row, col]
                matchOpposite = matchOpposite
                        && expectedSolutions[0][row, col] == solutions[1][row, col]
                        && expectedSolutions[1][row, col] == solutions[0][row, col]
            }

        assertTrue(match.xor(matchOpposite))
    }
}