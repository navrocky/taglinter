import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import okio.FileSystem

class Cli : CliktCommand(name = "taglinter") {
    override fun run() = Unit
}

fun commonMain(args: Array<String>, fs: FileSystem) {
    Cli().subcommands(CheckCommand(fs)).main(args)
}
