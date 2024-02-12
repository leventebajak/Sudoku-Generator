import com.leventebajak.sudoku.*

fun main() {
    val difficulty = Difficulty.entries.random()
    println("Difficulty: $difficulty")

    val sudoku = Sudoku.generate(difficulty)

    println("Clues:")
    sudoku.clues.print()

    for ((index, solution) in sudoku.solutions.withIndex()) {
        println("Solution ${index + 1} of ${sudoku.solutions.size}:")
        solution.print()
    }
}