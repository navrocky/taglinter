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

class FixCommand(fs: FileSystem) : BaseInspectionCommand(
    fs = fs,
    name = "fix",
    help = "Fix duplicates"
) {
    private val dryRun: Boolean by option("--dry-run").flag()
        .help("Do not write changes to files")

    override fun processDuplicates(duplicates: List<TagChecker.DuplicatesInfo>, tagChecker: TagChecker) {
        val t = Terminal()
        if (duplicates.isEmpty()) {
            t.println(TextColors.brightGreen("\uD83D\uDC4D There are no duplicates to fix"))
            return
        }

        val existingTags = tagChecker.allTags.keys.toMutableSet()
        val files = mutableMapOf<String, FileContent>()

        val toFix = duplicates.sumOf { d -> d.recs.size - 1 }
        t.println(TextColors.brightYellow("Fixing ${TextColors.brightWhite(toFix.toString())} duplicate(s):"))
        duplicates.forEach { duplicate ->
            val recsToCorrect = duplicate.recs.subList(1, duplicate.recs.size)
            recsToCorrect.forEach { tagRecord ->
                val fileContent = files.getOrPut(tagRecord.file) {
                    fs.read(tagRecord.file.toPath()) { readUtf8() }.split("\n").toMutableList()
                }
                val newTag = generateTag(existingTags)
                t.println(
                    "✔ ${TextColors.brightWhite(duplicate.tag)} -> ${TextColors.brightWhite(newTag)} : ${tagRecord.file}:${tagRecord.lineIndex}:${tagRecord.pos}"
                )
                existingTags.add(newTag)
                val line = fileContent[tagRecord.lineIndex]
                val newLine = line.replaceRange(tagRecord.pos, tagRecord.pos + newTag.length, newTag)
                fileContent[tagRecord.lineIndex] = newLine
                logger.debug { "<8355a4ae> New line: $newLine" }
            }
        }

        t.println(TextColors.brightYellow("Writing ${TextColors.brightWhite(files.size.toString())} file(s):"))
        files.forEach { file ->
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
            t.println(TextColors.brightGreen("✅ All duplicates fixed"))
        } else {
            t.println(TextColors.brightYellow("❌ Changes have not been saved. Dry-run enabled"))
        }
    }
}