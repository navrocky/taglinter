import okio.FileSystem

fun main(args: Array<String>) {
    val fs = FileSystem.SYSTEM
    commonMain(args, fs)
}
