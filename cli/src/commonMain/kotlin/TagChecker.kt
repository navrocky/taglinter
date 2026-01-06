import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger(TagChecker::class.simpleName!!)

class TagChecker(
    private val fs: FileSystem,
    private val invalidTagRx: Regex? = null
) {
    private val tagRx = "<[0-9a-f]{8}>".toRegex()

    data class TagRecord(
        val tag: String,
        val file: String,
        val line: String,
        val lineIndex: Int,
        val pos: Int,
        val isValid: Boolean
    )

    data class DuplicatesInfo(
        val tag: String,
        val recs: List<TagRecord>
    )

    private val tags = mutableListOf<TagRecord>()

    private fun isValidTag(tag: String): Boolean = tagRx.matches(tag)

    private fun addTag(tag: String, file: String, line: String, lineIndex: Int, posInLine: Int, isValid: Boolean) {
        val rec = TagRecord(
            tag = tag,
            file = file,
            line = line,
            lineIndex = lineIndex,
            pos = posInLine,
            isValid = isValid
        )
        tags.add(rec)
        logger.debug { "<214064cc> Found ${if (isValid) "valid" else "invalid"} tag: $rec" }
    }

    fun collect(file: String) {
        val lines = fs.read(file.toPath()) { readUtf8() }.split("\n")
        logger.debug { "<2b18c522> Read file $file with ${lines.size} lines" }
        lines.forEachIndexed { lineIndex, line ->

            invalidTagRx?.findAll(line)?.forEach { match ->
                val tag = match.value
                if (!isValidTag(tag)) {
                    addTag(match.value, file, line, lineIndex, match.range.first, false)
                }
            }

            tagRx.findAll(line).forEach { match ->
                addTag(match.value, file, line, lineIndex, match.range.first, true)
            }
        }
    }

    val allTags: List<TagRecord> get() = tags

    fun getDuplicates(): List<DuplicatesInfo> {
        val res = mutableListOf<DuplicatesInfo>()
        tags.filter { it.isValid }.groupBy { it.tag }.forEach { (tag, records) ->
            if (records.size > 1) {
                res.add(DuplicatesInfo(tag, records))
            }
        }
        return res
    }

    fun getInvalidTags(): List<TagRecord> = tags.filter { !it.isValid }
}