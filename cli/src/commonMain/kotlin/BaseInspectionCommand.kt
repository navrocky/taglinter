import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem

private val logger = KotlinLogging.logger(CheckCommand::class.simpleName!!)

abstract class BaseInspectionCommand(
    protected val fs: FileSystem,
    name: String,
    help: String
) : CliktCommand(name = name, help = help) {
    private val patternsOption: List<String> by option("-p", "--pattern").multiple()
        .help("Specify regex pattern to match files. Matches all files by default")
    private val dirsOption: List<String> by option("-d", "--dir").multiple()
        .help("Specify root directory to search files. Current directory used by default")

    protected abstract fun processDuplicates(duplicates: List<TagChecker.DuplicatesInfo>, tagChecker: TagChecker)

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
        processDuplicates(duplicates, tagChecker)
    }
}
