package com.leventebajak.sudoku.dlx

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test for the [DLX] class.
 */
class DLXTest {
    @Test
    fun test() {
        val matrix = Matrix(
            """
                0010110
                1001001
                0110010
                1001000
                0100001
                0001101
            """
        )
        val dlx = DLX(matrix)
        val expectedSolution = Matrix(
            """
                1001000
                0010110
                0100001
            """.trimIndent()
        )
        val solutions = dlx.findAllSolutions().toList()
        assertEquals(1, solutions.size)
        assertEquals(expectedSolution, solutions[0])
    }
}