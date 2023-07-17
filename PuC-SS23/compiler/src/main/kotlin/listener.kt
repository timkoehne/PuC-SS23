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
            highlights.add(Highlight("parameter", ctx.param.startIndex, ctx.param.stopIndex + 1, 0, 0))
            highlights.add(Highlight("interface", ctx.tyParam.getStart().startIndex, ctx.tyParam.getStop().stopIndex + 1, 0, 0))
            super.enterFnParam(ctx)
        }

        override fun enterIf(ctx: PucParser.IfContext) {
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.condition.getStart().startIndex, 0, 0))
            highlights.add(Highlight("keyword", ctx.condition.getStop().stopIndex + 1, ctx.thenBranch.getStart().startIndex, 0, 0))
            highlights.add(Highlight("keyword", ctx.thenBranch.getStop().stopIndex + 1, ctx.elseBranch.getStart().startIndex, 0, 0))
            super.enterIf(ctx)
        }

        override fun enterProg(ctx: PucParser.ProgContext) {
            super.enterProg(ctx)
//            highlights.add(Highlight("prog", ctx.getStart().startIndex, ctx.getStop().stopIndex, 0, 0))
        }

        override fun enterFnDef(ctx: PucParser.FnDefContext) {
            if( ctx.tyVars() == null){
                highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.name.startIndex, 0, 0))
            }else{
                highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.tyVars().getStart().startIndex, 0, 0))
            }
            highlights.add(Highlight("function", ctx.name.startIndex, ctx.name.stopIndex+1, 0, 0))
            super.enterFnDef(ctx)
        }

        override fun enterInit(ctx: PucParser.InitContext) {
            super.enterInit(ctx)
        }

        override fun enterTyVars(ctx: PucParser.TyVarsContext) {
            for (parameter in ctx.NAME()){
                highlights.add(Highlight("variable", parameter.symbol.startIndex, parameter.symbol.stopIndex+1, 0, 0))
            }
            super.enterTyVars(ctx)
        }

        override fun enterTypeDef(ctx: PucParser.TypeDefContext) {
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.name.startIndex, 0, 0))
            highlights.add(Highlight("type", ctx.name.startIndex, ctx.name.stopIndex + 1, 0, 0))
            super.enterTypeDef(ctx)
        }

        override fun enterTypeConstructor(ctx: PucParser.TypeConstructorContext) {
            highlights.add(Highlight("type", ctx.getStart().startIndex, ctx.constr.stopIndex+1, 0, 0))
            super.enterTypeConstructor(ctx)
        }

        override fun enterApp(ctx: PucParser.AppContext) {
            highlights.add(Highlight("function", ctx.getStart().startIndex, ctx.fn.getStop().stopIndex+1, 0, 0))
//            highlights.add(Highlight("variable", ctx.arg.getStart().startIndex, ctx.arg.getStop().stopIndex+1, 0, 0))

            super.enterApp(ctx)
        }

        override fun enterConstruction(ctx: PucParser.ConstructionContext) {
            highlights.add(Highlight("type", ctx.getStart().startIndex, ctx.typ.stopIndex + 1, 0, 0))
            highlights.add(Highlight("type", ctx.constr.startIndex, ctx.constr.stopIndex + 1, 0, 0))
            super.enterConstruction(ctx)
        }

        override fun enterVar(ctx: PucParser.VarContext) {
            highlights.add(Highlight("variable", ctx.getStart().startIndex, ctx.getStop().stopIndex + 1, 0, 0))
            super.enterVar(ctx)
        }

        override fun enterParenthesized(ctx: PucParser.ParenthesizedContext) {
            super.enterParenthesized(ctx)
        }

        override fun enterLet(ctx: PucParser.LetContext) {
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.NAME().symbol.startIndex, 0, 0))
            highlights.add(Highlight("variable", ctx.NAME().symbol.startIndex, ctx.NAME().symbol.stopIndex+1, 0, 0))
            highlights.add(Highlight("keyword", ctx.bound.getStop().stopIndex + 1, ctx.body.getStart().startIndex, 0, 0))
            super.enterLet(ctx)
        }

        override fun enterIntLit(ctx: PucParser.IntLitContext) {
            highlights.add(Highlight("number", ctx.getStart().startIndex, ctx.getStop().stopIndex+1, 0, 0))
            super.enterIntLit(ctx)
        }

        override fun enterLambda(ctx: PucParser.LambdaContext) {
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.param.startIndex, 0, 0))
            highlights.add(Highlight("parameter", ctx.param.startIndex, ctx.param.stopIndex+1, 0, 0))
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
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.scrutinee.getStart().startIndex, 0, 0))
            highlights.add(Highlight("variable", ctx.scrutinee.getStart().startIndex, ctx.scrutinee.getStop().stopIndex+1, 0, 0))
            super.enterCase(ctx)
        }

        override fun enterCaseBranch(ctx: PucParser.CaseBranchContext) {
            highlights.add(Highlight("keyword", ctx.getStart().startIndex, ctx.pattern().getStart().startIndex, 0, 0))
            super.enterCaseBranch(ctx)
        }

        override fun enterPattern(ctx: PucParser.PatternContext) {
            highlights.add(Highlight("type", ctx.typ.startIndex, ctx.typ.stopIndex+1, 0, 0))
            highlights.add(Highlight("type", ctx.constr.startIndex, ctx.constr.stopIndex+1, 0, 0))
            for (parameter in ctx.NAME()){
                highlights.add(Highlight("parameter", parameter.symbol.startIndex, parameter.symbol.stopIndex+1, 0, 0))
            }
            super.enterPattern(ctx)
        }

        override fun enterBinary(ctx: PucParser.BinaryContext) {
            super.enterBinary(ctx)
        }

        override fun enterUnary(ctx: PucParser.UnaryContext) {
            super.enterUnary(ctx)
        }

        override fun enterTyBool(ctx: PucParser.TyBoolContext) {
            highlights.add(Highlight("interface", ctx.getStart().startIndex, ctx.getStop().stopIndex+1, 0, 0))
            super.enterTyBool(ctx)
        }

        override fun enterTyConstructor(ctx: PucParser.TyConstructorContext) {
            highlights.add(Highlight("type", ctx.getStart().startIndex, ctx.getStop().stopIndex+1, 0, 0))
            super.enterTyConstructor(ctx)
        }

        override fun enterTyText(ctx: PucParser.TyTextContext) {
            highlights.add(Highlight("interface", ctx.getStart().startIndex, ctx.getStop().stopIndex+1, 0, 0))
            super.enterTyText(ctx)
        }

        override fun enterTyParenthesized(ctx: PucParser.TyParenthesizedContext) {
            super.enterTyParenthesized(ctx)
        }

        override fun enterTyVar(ctx: PucParser.TyVarContext) {
            super.enterTyVar(ctx)
        }

        override fun enterTyInt(ctx: PucParser.TyIntContext) {
            highlights.add(Highlight("interface", ctx.getStart().startIndex, ctx.getStop().stopIndex+1, 0, 0))
            super.enterTyInt(ctx)
        }

        override fun enterTyFun(ctx: PucParser.TyFunContext) {
            super.enterTyFun(ctx)
        }
    }
}

