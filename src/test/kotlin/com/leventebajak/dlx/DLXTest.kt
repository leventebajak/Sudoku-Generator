package com.leventebajak.dlx

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test for the [DLX] class.
 */
class DLXTest {
    @Test
    fun test() {
        val matrix = """
                0010110
                1001001
                0110010
                1001000
                0100001
                0001101
            """.toMatrix()

        val dlx = DLX(matrix)
        val solutions = dlx.findAllSolutions().toList()

        val expectedSolution = """
                1001000
                0010110
                0100001
            """.toMatrix()
        assertEquals(1, solutions.size)
        assertEquals(expectedSolution, solutions[0])
    }
}