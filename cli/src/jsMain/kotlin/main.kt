import okio.NodeJsFileSystem

external val process: dynamic

fun main() {
    val argv = process.argv.slice(2) as Array<String>
    val fs = NodeJsFileSystem
    commonMain(argv, fs)
}
