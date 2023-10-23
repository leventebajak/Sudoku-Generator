import Sudoku.Companion.print

fun main() {
    val sudoku = Sudoku(Sudoku.Difficulty.MEDIUM)
    println("Clues:")
    sudoku.clues.print()
    println("Solution:")
    sudoku.solution.print()
}