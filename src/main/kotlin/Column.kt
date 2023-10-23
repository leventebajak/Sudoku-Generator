/**
 * A [Node] that represents a column header in the doubly-linked list in [DLX].
 *
 * @property id The id of the column.
 * @property size The number of [Node]s in this column.
 */
class Column(val id: Int) : Node() {
    var size: Int = 0

    override fun toString(): String {
        return "Column(id=$id, size=$size)"
    }
}