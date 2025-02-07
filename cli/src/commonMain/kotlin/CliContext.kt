import io.github.oshai.kotlinlogging.Level
import okio.FileSystem

class CliContext(
    val args: Array<String>,
    val fs: FileSystem,
    val setLogLevel: (Level) -> Unit
)
