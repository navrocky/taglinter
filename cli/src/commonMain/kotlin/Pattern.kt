interface Pattern {
    fun check(fileName: String): Boolean
}

class RegExPattern(regex: String) : Pattern {
    private val rx: Regex = Regex(regex)
    override fun check(fileName: String): Boolean = rx.containsMatchIn(fileName)
    override fun toString(): String {
        return rx.toString()
    }
}
