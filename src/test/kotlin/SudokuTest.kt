import Sudoku.Companion.toBoard
import org.junit.jupiter.api.Assertions.*

/**
 * Test for the [Sudoku] class.
 */
class SudokuTest {
    @org.junit.jupiter.api.Test
    fun test() {
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

        val solution = Sudoku(board).solution

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
                assertEquals(expectedSolution[row][col], solution[row][col])
    }
}