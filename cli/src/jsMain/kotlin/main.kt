import okio.NodeJsFileSystem

external val process: dynamic

fun main() {
    commonMain(CliContext(
        args = process.argv.slice(2) as Array<String>,
        fs = NodeJsFileSystem,
        setLogLevel = {
            // TODO: Set log level
        }
    ))
}
