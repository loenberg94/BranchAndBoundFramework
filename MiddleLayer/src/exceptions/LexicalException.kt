package exceptions

class LexicalException(override val message: String?, val position:Int, val row:Int, val column:Int): Exception() {
    override fun toString(): String {
        return "Token doesn't match: $message at $position ($row:$column)"
    }
}