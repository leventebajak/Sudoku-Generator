package dlx

/**
 * A [Node] that represents a column header in the circular doubly-linked list in [DLX].
 *
 * @property id The id of the column.
 * @property size The number of [Node]s in this column.
 */
class Column(val id: Int) : Node() {
    var size: Int = 0

    /**
     * Covering the [Column].
     */
    fun cover() {
        right.left = left
        left.right = right
        size--
        var i = down
        while (i != this) {
            var j = i.right
            while (j != i) {
                j.down.up = j.up
                j.up.down = j.down
                j.column!!.size--
                j = j.right
            }
            i = i.down
        }
    }

    /**
     * Uncovering the [Column].
     */
    fun uncover() {
        var i = up
        while (i != this) {
            var j = i.left
            while (j != i) {
                j.column!!.size++
                j.down.up = j
                j.up.down = j
                j = j.left
            }
            i = i.up
        }
        right.left = this
        left.right = this
        size++
    }

    override fun toString(): String {
        return "Column(id=$id, size=$size)"
    }
}