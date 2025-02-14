import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import okio.FileSystem

class CheckCommand(fs: FileSystem) : BaseInspectionCommand(
    fs = fs,
    name = "check",
    help = "Run check"
) {
    override fun processDuplicates(duplicates: List<TagChecker.DuplicatesInfo>, tagChecker: TagChecker) {
        val t = Terminal()
        if (duplicates.isNotEmpty()) {
            duplicates.forEach { info ->
                t.println(TextColors.brightRed("Tag ${TextColors.brightYellow(info.tag)} is duplicates in the following files"))
                info.recs.forEach { r ->
                    t.println(TextColors.green("  ${r.file}:${r.lineIndex}:${r.pos}"))
                }
                t.println()
            }
            t.println(TextColors.brightRed("\uD83D\uDC4E ${duplicates.size} duplicate tags found"))
            throw ProgramResult(1)
        } else {
            t.println(TextColors.brightGreen("👍 No duplicate tags found"))
        }
    }
}
