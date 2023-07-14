import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

class HighlightListener {

    val highlights = mutableListOf<Highlight>()

    fun parseAndWalk(sourceCode: String) {
        val lexer = PucLexer(CharStreams.fromString(sourceCode))
        val tokens = CommonTokenStream(lexer)
        val parser = PucParser(tokens)
        val tree = parser.init()

        val listener = ParseListener(highlights)
        ParseTreeWalker.DEFAULT.walk(listener, tree)
    }

    private class ParseListener(val highlights: MutableList<Highlight>) : PucBaseListener() {
        override fun enterFnParam(ctx: PucParser.FnParamContext) {
            highlights.add(Highlight("parameter", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
        }

        override fun enterIf(ctx: PucParser.IfContext) {
            super.enterIf(ctx)
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
        }


    }
}

