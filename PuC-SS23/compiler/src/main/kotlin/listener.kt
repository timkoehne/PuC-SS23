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
//            highlights.add(Highlight("parameter", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
        }

        override fun enterIf(ctx: PucParser.IfContext) {
            super.enterIf(ctx)

            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.condition.getStart().startIndex, 0, 0)) //if
            highlights.add(Highlight("keyword", ctx.condition.getStop().stopIndex + 1, ctx.thenBranch.getStart().startIndex, 0, 0)) //then
            highlights.add(Highlight("keyword", ctx.thenBranch.getStop().stopIndex + 1, ctx.elseBranch.getStart().startIndex, 0, 0)) //else
        }

        override fun enterProg(ctx: PucParser.ProgContext) {
            super.enterProg(ctx)
//            highlights.add(Highlight("prog", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
        }

        override fun enterFnDef(ctx: PucParser.FnDefContext) {
//            highlights.add(Highlight("prog", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
            super.enterFnDef(ctx)
        }

        override fun enterInit(ctx: PucParser.InitContext) {
//            highlights.add(Highlight("init", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
            super.enterInit(ctx)
        }

        override fun enterTyVars(ctx: PucParser.TyVarsContext) {
//            highlights.add(Highlight("variable", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
            super.enterTyVars(ctx)
        }

        override fun enterTypeDef(ctx: PucParser.TypeDefContext) {
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.name.startIndex, 0, 0)) // type
            highlights.add(Highlight("struct", ctx.name.startIndex, ctx.name.stopIndex + 1, 0, 0)) // name
            super.enterTypeDef(ctx)
        }

        override fun enterTypeConstructor(ctx: PucParser.TypeConstructorContext) {
            highlights.add(Highlight("type", ctx.getStart().startIndex, ctx.constr.stopIndex+1, 0, 0)) //constr
            super.enterTypeConstructor(ctx)
        }

        override fun enterApp(ctx: PucParser.AppContext) {
            super.enterApp(ctx)
        }

        override fun enterConstruction(ctx: PucParser.ConstructionContext) {
            highlights.add(Highlight("struct", ctx.getStart().startIndex, ctx.typ.stopIndex + 1, 0, 0))
            highlights.add(Highlight("type", ctx.constr.startIndex, ctx.constr.stopIndex + 1, 0, 0))
            super.enterConstruction(ctx)
        }

        override fun enterVar(ctx: PucParser.VarContext) {
            super.enterVar(ctx)
        }

        override fun enterParenthesized(ctx: PucParser.ParenthesizedContext) {
            super.enterParenthesized(ctx)
        }

        override fun enterLet(ctx: PucParser.LetContext) {
            super.enterLet(ctx)
        }

        override fun enterIntLit(ctx: PucParser.IntLitContext) {
//            highlights.add(Highlight("number", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
            super.enterIntLit(ctx)
        }

        override fun enterLambda(ctx: PucParser.LambdaContext) {
            super.enterLambda(ctx)
        }

        override fun enterBoolLit(ctx: PucParser.BoolLitContext) {
            highlights.add(Highlight("regexp", ctx.getStart().startIndex, ctx.getStop().stopIndex + 1, 0, 0))
            super.enterBoolLit(ctx)
        }
        
        override fun enterTextLit(ctx: PucParser.TextLitContext) {
            highlights.add(Highlight("string", ctx.getStart().startIndex, ctx.getStop().stopIndex + 1, 0, 0))
            super.enterTextLit(ctx)
        }

        override fun enterCase(ctx: PucParser.CaseContext) {
            super.enterCase(ctx)
        }

        override fun enterCaseBranch(ctx: PucParser.CaseBranchContext) {
            super.enterCaseBranch(ctx)
        }

        override fun enterPattern(ctx: PucParser.PatternContext) {
            super.enterPattern(ctx)
        }

        override fun enterBinary(ctx: PucParser.BinaryContext) {
            super.enterBinary(ctx)
        }

        override fun enterUnary(ctx: PucParser.UnaryContext) {
            super.enterUnary(ctx)
        }

        override fun enterTyBool(ctx: PucParser.TyBoolContext) {
            super.enterTyBool(ctx)
        }

        override fun enterTyConstructor(ctx: PucParser.TyConstructorContext) {
            super.enterTyConstructor(ctx)
        }

        override fun enterTyText(ctx: PucParser.TyTextContext) {
            super.enterTyText(ctx)
        }

        override fun enterTyParenthesized(ctx: PucParser.TyParenthesizedContext) {
            super.enterTyParenthesized(ctx)
        }

        override fun enterTyVar(ctx: PucParser.TyVarContext) {
            super.enterTyVar(ctx)
        }

        override fun enterTyInt(ctx: PucParser.TyIntContext) {
            super.enterTyInt(ctx)
        }

        override fun enterTyFun(ctx: PucParser.TyFunContext) {
            super.enterTyFun(ctx)
        }
    }
}

