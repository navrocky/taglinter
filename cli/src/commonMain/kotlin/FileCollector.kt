import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger("FileCollector")

private data class Context(
    val fs: FileSystem,
    val patterns: List<Pattern>,
    val consumer: (file: String) -> Unit
)

private fun collectFilesImpl(ctx: Context, dir: Path) {
    try {
        logger.debug { "<7eec5400> Iterate directory: $dir" }
        ctx.fs.list(dir).forEach { path ->
            val meta = ctx.fs.metadata(path)
            when {
                meta.isDirectory -> collectFilesImpl(ctx, path)
                meta.isRegularFile -> {
                    if (ctx.patterns.any { it.check(path.name) })
                        ctx.consumer(path.toString())
                }

                else -> {
                    //skip unknown files
                }
            }
        }
    } catch (e: IOException) {
        logger.error(e) { "<0ddf8bd7> Cannot iterate directory '$dir'" }
    }
}

fun collectFiles(
    fs: FileSystem,
    startDirectory: String,
    patterns: List<Pattern>,
    consumer: (file: String) -> Unit
) {
    collectFilesImpl(Context(fs, patterns, consumer), startDirectory.toPath(normalize = true))
}
