fun generateTag(existingTags: Set<String>): String {
    val chars = "0123456789abcdef"
    while (true) {
        val tag = "<${(1..8).map { chars.random() }.joinToString("")}>"
        if (!existingTags.contains(tag)) return tag
    }
}