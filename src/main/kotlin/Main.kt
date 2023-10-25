import sudoku.*

fun main() {
    val sudoku = SudokuGenerator.generate(SudokuGenerator.Difficulty.EASY)
    println("Clues:")
    sudoku.clues.print()
    for (solution in sudoku.solutions) {
        println("Solution ${sudoku.solutions.indexOf(solution) + 1} of ${sudoku.solutions.size}:")
        solution.print()
    }
}