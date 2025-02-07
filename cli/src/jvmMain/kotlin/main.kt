import io.github.oshai.kotlinlogging.Level
import okio.FileSystem

fun main(args: Array<String>) {
    val root =
        org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
    root.level = ch.qos.logback.classic.Level.INFO
    commonMain(CliContext(
        args = args,
        fs = FileSystem.SYSTEM,
        setLogLevel = {
            root.level = when (it) {
                Level.TRACE -> ch.qos.logback.classic.Level.TRACE
                Level.DEBUG -> ch.qos.logback.classic.Level.DEBUG
                Level.INFO -> ch.qos.logback.classic.Level.INFO
                Level.WARN -> ch.qos.logback.classic.Level.WARN
                Level.ERROR -> ch.qos.logback.classic.Level.ERROR
                Level.OFF -> ch.qos.logback.classic.Level.OFF
            }
        }
    ))
}
