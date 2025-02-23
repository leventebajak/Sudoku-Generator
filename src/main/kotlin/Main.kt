import com.leventebajak.sudoku.*

fun main() {
    val difficulty = Difficulty.entries.random()
    println("Difficulty: $difficulty")

    val puzzle = Puzzle(difficulty)

    println("Clues:")
    println(puzzle.clues)

    println("Solution:")
    println(puzzle.solution)
}