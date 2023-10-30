package sudoku

/**
 * A 9x9 Sudoku board with a list of solutions.
 *
 * @property clues The [Board] with the clues.
 * @property solutions The solutions to the [Board] with the [clues].
 */
class Sudoku(val clues: Board) {
    val solutions = SudokuSolver.findAllSolutionsFor(clues)
}