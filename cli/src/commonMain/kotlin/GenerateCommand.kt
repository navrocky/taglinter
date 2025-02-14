import com.github.ajalt.clikt.core.CliktCommand

class GenerateCommand : CliktCommand(name = "generate", help = "Generates new tag") {
    override fun run() {
        println(generateTag(emptySet()))
    }
}