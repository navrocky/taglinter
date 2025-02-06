import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem

private val logger = KotlinLogging.logger(CheckCommand::class.simpleName!!)

class CheckCommand(private val fs: FileSystem) : CliktCommand(name = "check", help = "Run check") {
    private val patternsOption: List<String> by option("-p", "--pattern").multiple()
        .help("Specify regex pattern to match files. Matches all files by default")
    private val dirsOption: List<String> by option("-d", "--dir").multiple()
        .help("Specify root directory to search files. Current directory used by default")

    private fun printDuplicates(t: Terminal, duplicates: List<TagChecker.DuplicatesInfo>) {
        duplicates.forEach { info ->
            t.println(TextColors.brightRed("Tag ${TextColors.brightYellow(info.tag)} is duplicates in the following files"))
            info.recs.forEach { r ->
                t.println(TextColors.green("  ${r.file}:${r.lineIndex}:${r.pos}"))
            }
            t.println()
        }
        t.println(TextColors.brightRed("\uD83D\uDC4E ${duplicates.size} duplicate tags found"))
    }

    override fun run() {
        val tagChecker = TagChecker(fs)
        val dirs = dirsOption.ifEmpty { listOf(".") }
        val patterns = patternsOption.ifEmpty { listOf(".+") }.map { RegExPattern(it) }
        logger.debug { "<d075d147> Check files. dirs: $dirs, patterns: $patterns" }
        dirs.forEach { dir ->
            collectFiles(fs, dir, patterns) {
                tagChecker.collect(it)
            }
        }
        val duplicates = tagChecker.getDuplicates()
        val t = Terminal()
        if (duplicates.isNotEmpty()) {
            printDuplicates(t, duplicates)
            throw ProgramResult(1)
        } else {
            t.println(TextColors.brightGreen("👍 No duplicate tags found"))
        }
    }
}
