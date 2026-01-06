import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger(FixCommand::class.simpleName!!)

typealias FileContent = MutableList<String>

private class Files(val fs: FileSystem) {

    private val files = mutableMapOf<String, FileContent>()

    fun getFileContext(path: String): FileContent =
        files.getOrPut(path) {
            fs.read(path.toPath()) { readUtf8() }.split("\n").toMutableList()
        }

    fun getAll(): Map<String, FileContent> = files
}

class FixCommand(fs: FileSystem) : BaseInspectionCommand(
    fs = fs,
    name = "fix",
    help = "Fix tags"
) {
    private val dryRun: Boolean by option("--dry-run").flag()
        .help("Do not write changes to files")

    private val disableDuplicatesOption: Boolean by option("--no-duplicates").flag()
        .help("Do not fix duplicates")
    private val disableInvalidOption: Boolean by option("--no-invalid").flag()
        .help("Do not fix invalid tags")

    private fun fixTag(t: Terminal, tagRecord: TagChecker.TagRecord, files: Files, existingTags: MutableSet<String>) {
        val fileContent = files.getFileContext(tagRecord.file)
        val newTag = generateTag(existingTags)
        t.println(
            "✔ ${TextColors.brightWhite(tagRecord.tag)} -> ${TextColors.brightWhite(newTag)} : ${tagRecord.file}:${tagRecord.lineIndex + 1}:${tagRecord.pos + 1}"
        )
        existingTags.add(newTag)
        val line = fileContent[tagRecord.lineIndex]
        val newLine = line.replaceRange(tagRecord.pos, tagRecord.pos + newTag.length, newTag)
        fileContent[tagRecord.lineIndex] = newLine
        logger.debug { "<8355a4ae> New line: $newLine" }
    }

    private fun fixDuplicates(
        t: Terminal,
        tagChecker: TagChecker,
        existingTags: MutableSet<String>,
        files: Files
    ) {
        val duplicates = tagChecker.getDuplicates()
        if (duplicates.isEmpty()) {
            t.println(TextColors.brightGreen("\uD83D\uDC4D There are no duplicates to fix"))
            return
        }

        val toFix = duplicates.sumOf { d -> d.recs.size - 1 }
        t.println(TextColors.brightYellow("Fixing ${TextColors.brightWhite(toFix.toString())} duplicate(s):"))
        duplicates.forEach { duplicate ->
            val recsToCorrect = duplicate.recs.subList(1, duplicate.recs.size)
            recsToCorrect.forEach { tagRecord ->
                fixTag(t, tagRecord, files, existingTags)
            }
        }
    }

    private fun fixInvalidTags(t: Terminal, tagChecker: TagChecker, existingTags: MutableSet<String>, files: Files) {
        val invalidTags = tagChecker.getInvalidTags()
        if (invalidTags.isEmpty()) {
            t.println(TextColors.brightGreen("\uD83D\uDC4D There are no invalid tags to fix"))
            return
        }
        val toFix = invalidTags.size
        t.println(TextColors.brightYellow("Fixing ${TextColors.brightWhite(toFix.toString())} invalid tag(s):"))
        invalidTags.forEach { tagRecord ->
            fixTag(t, tagRecord, files, existingTags)
        }
    }

    private fun writeFiles(t: Terminal, files: Files) {
        val allFiles = files.getAll()
        t.println(TextColors.brightYellow("Writing ${TextColors.brightWhite(allFiles.size.toString())} file(s):"))
        allFiles.forEach { file ->
            t.println(
                "✔ ${TextColors.brightWhite(file.key)}"
            )
            if (!dryRun) {
                fs.write(file.key.toPath()) {
                    writeUtf8(file.value.joinToString("\n"))
                }
            }
        }
        if (!dryRun) {
            t.println(TextColors.brightGreen("✅ All errors fixed and changes saved"))
        } else {
            t.println(TextColors.brightYellow("❌ Changes have not been saved. Dry-run enabled"))
        }
    }

    override fun process(tagChecker: TagChecker): Boolean {
        val t = Terminal()
        val existingTags = tagChecker.allTags.map { it.tag }.toMutableSet()
        val files = Files(fs)

        if (!disableDuplicatesOption) {
            fixDuplicates(t, tagChecker, existingTags, files)
        }

        if (!disableInvalidOption) {
            fixInvalidTags(t, tagChecker, existingTags, files)
        }

        writeFiles(t, files)
        return true
    }
}