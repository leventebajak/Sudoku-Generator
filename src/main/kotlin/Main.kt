import sudoku.*

fun main() {
    val sudoku = SudokuGenerator.generate(SudokuGenerator.Difficulty.EASY)
    println("Clues:")
    sudoku.clues.print()
    for ((index, solution) in sudoku.solutions.withIndex()) {
        println("Solution ${index + 1} of ${sudoku.solutions.size}:")
        solution.print()
    }
}