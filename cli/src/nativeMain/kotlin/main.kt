import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import okio.FileSystem

fun main(args: Array<String>) {
    KotlinLoggingConfiguration.logLevel = Level.INFO
    commonMain(CliContext(
        args = args,
        fs = FileSystem.SYSTEM,
        setLogLevel = {
            KotlinLoggingConfiguration.logLevel = it
        }
    ))
}
