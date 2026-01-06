import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import okio.FileSystem

class CheckCommand(fs: FileSystem) : BaseInspectionCommand(
    fs = fs,
    name = "check",
    help = "Run check"
) {
    private fun checkDuplicates(t: Terminal, tagChecker: TagChecker): Boolean {
        val duplicates = tagChecker.getDuplicates()
        if (duplicates.isEmpty()) {
            t.println(TextColors.brightGreen("👍 No duplicate tags found"))
            return true
        }
        duplicates.forEach { info ->
            t.println(TextColors.brightRed("Tag ${TextColors.brightYellow(info.tag)} is duplicates in the following files"))
            info.recs.forEach { r ->
                t.println(TextColors.green("  ${r.file}:${r.lineIndex + 1}:${r.pos + 1}"))
            }
            t.println()
        }
        t.println(TextColors.brightRed("\uD83D\uDC4E ${duplicates.size} duplicate tags found"))
        return false
    }

    private fun checkInvalidTags(t: Terminal, tagChecker: TagChecker): Boolean {
        val invalidTags = tagChecker.getInvalidTags()
        if (invalidTags.isEmpty()) {
            t.println(TextColors.brightGreen("👍 No invalid tags found"))
            return true
        }
        invalidTags.groupBy { it.tag }.forEach { info ->
            t.println(TextColors.brightRed("Tag ${TextColors.brightYellow(info.key)} is invalid in the following files"))
            info.value.forEach { r ->
                t.println(TextColors.green("  ${r.file}:${r.lineIndex + 1}:${r.pos + 1}"))
            }
            t.println()
        }
        t.println(TextColors.brightRed("\uD83D\uDC4E ${invalidTags.size} invalid tags found"))
        return false
    }

    override fun process(tagChecker: TagChecker): Boolean {
        val t = Terminal()
        val noDuplicates = checkDuplicates(t, tagChecker)
        t.println()
        val noInvalid = checkInvalidTags(t, tagChecker)
        return noDuplicates && noInvalid
    }
}
