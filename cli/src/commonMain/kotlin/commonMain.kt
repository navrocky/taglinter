import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import io.github.oshai.kotlinlogging.Level

class Cli(private val ctx: CliContext) : CliktCommand(name = "taglinter") {
    private val debug by option("-d", "--debug").flag().help(help = "Enable debug logging")

    init {
        completionOption()
    }

    override fun run() {
        if (debug) {
            ctx.setLogLevel(Level.TRACE)
        }
    }
}

fun commonMain(ctx: CliContext) {
    Cli(ctx).subcommands(
        CheckCommand(ctx.fs),
        FixCommand(ctx.fs),
        GenerateCommand()
    ).main(ctx.args)
}
