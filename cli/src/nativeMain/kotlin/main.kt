import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import okio.FileSystem

fun main(args: Array<String>) {
    KotlinLoggingConfiguration.logLevel = Level.DEBUG
    commonMain(args, FileSystem.SYSTEM)
}
