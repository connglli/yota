package io.github.clixyz.yota.cmds

import android.support.test.uiautomator.By
import io.github.clixyz.yota.droid.Droid
import io.github.clixyz.yota.droid.delegates.UiAutoDelegate
import io.github.clixyz.yota.utils.Command
import io.github.clixyz.yota.utils.Logger
import io.github.clixyz.yota.utils.OptParser
import io.github.clixyz.yota.utils.accessors.getTyped
import io.github.clixyz.yota.view.YotaViewDumper
import org.json.simple.JSONArray
import java.util.regex.Pattern

class YotaSelect : Command {
    override val name: String = "select"
    override val usage: String by lazy {
        "select: select views by attributes\n" +
        "\n" +
        "usage: \n" +
        "  yota select [-n <n>]\n" +
        "              [--idx <index>]\n" +
        "              [--cls <cls>] [--cls-matches <regexp>]\n" +
        "              [--pkg <pkg>] [--pkg-matches <regexp>]\n" +
        "              [--txt <text>] [--txt-matches <regexp>]\n" +
        "              [--txt-contains <str>] [--txt-contains-ignore-case <str>]\n" +
        "              [--txt-starts-with <str>] [--txt-ends-with <str>]\n" +
        "              [--desc <desc>] [--desc-matches <regexp>]\n" +
        "              [--desc-contains <str>] [--desc-contains-ignore-case <str>]\n" +
        "              [--desc-starts-with <str>] [--desc-ends-with <str>]\n" +
        "              [--res-id <id>] [--res-id-matches <regexp>]\n" +
        "              [--res-id-contains <str>] [--res-id-contains-ignore-case <str>]\n" +
        "              [--clickable] [--long-clickable]\n" +
        "              [--checkable] [--checked]\n" +
        "              [--focusable] [--focused]\n" +
        "              [--scrollable]\n" +
        "              [--selected]\n" +
        "              [--enabled]"
    }

    companion object {
        val SUCCEEDED = Command.Status(0, "succeeded")
        val FAILED_INSUFFICIENT_ARGS = Command.Status(1, "args are insufficient")
        val FAILED_NULL_ROOT = Command.Status(2, "root is null at present")
        @Suppress("FunctionName")
        fun FAILED_UNKNOWN_OPTION(opt: String) = Command.Status(3, "option $opt is unknown")
    }

    override fun exec(args: Array<String>): Command.Status {
        val parser = OptParser(args)
        val selector = By.newSelector()
        var compressed = false
        var n: Int? = null
        for (opt in parser) {
            when (opt) {
                "--compressed" -> compressed = true
                "-n" -> n = parser.getTyped(opt, Integer::parseInt) // select all views that matches, else select the very first
                "--idx" -> selector.index(Integer.parseInt(parser.get(opt)))
                "--cls" -> selector.clazz(parser.get(opt))
                "--cls-matches" -> selector.clazz(Pattern.compile(parser.get(opt)!!))
                "--pkg" -> selector.pkg(parser.get(opt))
                "--pkg-matches" -> selector.pkg(Pattern.compile(parser.get(opt)!!))
                "--txt" -> selector.text(parser.get(opt))
                "--txt-matches" -> selector.text(Pattern.compile(parser.get(opt)!!))
                "--txt-starts-with" -> selector.textStartsWith(parser.get(opt))
                "--txt-ends-with" -> selector.textEndsWith(parser.get(opt))
                "--txt-contains" -> selector.textContains(parser.get(opt))
                "--txt-contains-ignore-case" -> selector.textContainsIgnoreCase(parser.get(opt))
                "--desc" -> selector.desc(parser.get(opt))
                "--desc-matches" -> selector.desc(Pattern.compile(parser.get(opt)!!))
                "--desc-starts-with" -> selector.descStartsWith(parser.get(opt))
                "--desc-ends-with" -> selector.descEndsWith(parser.get(opt))
                "--desc-contains" -> selector.descContains(parser.get(opt))
                "--desc-contains-ignore-case" -> selector.descContainsIgnoreCase(parser.get(opt))
                "--res-id" -> selector.res(parser.get(opt))
                "--res-id-matches" -> selector.res(Pattern.compile(parser.get(opt)!!))
                "--res-id-contains" -> selector.resContains(parser.get(opt))
                "--res-id-contains-ignore-case" -> selector.resContainsIgnoreCase(parser.get(opt))
                "--clickable" -> selector.clickable(true)
                "--long-clickable" -> selector.longClickable(true)
                "--checkable" -> selector.checkable(true)
                "--checked" -> selector.checked(true)
                "--focusable" -> selector.focusable(true)
                "--focused" -> selector.focused(true)
                "--scrollable" -> selector.scrollable(true)
                "--enabled" -> selector.enabled(true)
                "--selected" -> selector.selected(true)
                else -> return FAILED_UNKNOWN_OPTION(opt)
            }
        }
        val found = try {
            Droid.exec {
                it.ua.compressed = compressed
                it.ua.findViews(selector)
            }
        } catch (x: UiAutoDelegate.NullRootException) {
            return FAILED_NULL_ROOT
        }
        if (found.isEmpty()) {
            Logger.println("[]")
            return SUCCEEDED
        }
        val selected = when {
            n == null -> found
            n < 0 -> listOf(found[0])
            n >= found.size -> listOf(found.last())
            else -> listOf(found[n])
        }
        Logger.println(JSONArray.toJSONString(selected.map(YotaViewDumper::viewToMap)))
        return SUCCEEDED
    }
}