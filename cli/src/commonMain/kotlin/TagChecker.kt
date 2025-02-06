import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger(TagChecker::class.simpleName!!)

class TagChecker(private val fs: FileSystem) {
    private val tagRx = "<[0-9a-f]{8}>".toRegex()

    data class TagRecord(
        val tag: String,
        val file: String,
        val line: String,
        val lineIndex: Int,
        val pos: Int
    )

    data class DuplicatesInfo(
        val tag: String,
        val recs: List<TagRecord>
    )

    private val tags = mutableMapOf<String, MutableList<TagRecord>>()

    fun collect(file: String) {
        val lines = fs.read(file.toPath()) { readUtf8() }.split("\n")
        logger.debug { "<2b18c522> Read file $file with ${lines.size} lines" }
        lines.forEachIndexed { lineIndex, line ->
            tagRx.findAll(line).forEach { match ->
                val rec = TagRecord(
                    tag = match.value,
                    file = file,
                    line = line,
                    lineIndex = lineIndex,
                    pos = match.range.first
                )
                val list = tags.getOrPut(rec.tag) { mutableListOf() }
                list.add(rec)
                logger.debug { "<214064cc> Found tag: $rec" }
            }
        }
    }

    fun getDuplicates(): List<DuplicatesInfo> {
        val res = mutableListOf<DuplicatesInfo>()
        tags.forEach { (tag, records) ->
            if (records.size > 1) {
                res.add(DuplicatesInfo(tag, records))
            }
        }
        return res
    }
}