# Generated from /Users/calebhuck/PycharmProjects/OpenMPy/preprocessor/Grammar.g4 by ANTLR 4.9.1
from antlr4 import *
if __name__ is not None and "." in __name__:
    from .GrammarParser import GrammarParser
else:
    from GrammarParser import GrammarParser

# This class defines a complete generic visitor for a parse tree produced by GrammarParser.

class GrammarVisitor(ParseTreeVisitor):

    # Visit a parse tree produced by GrammarParser#file_input.
    def visitFile_input(self, ctx:GrammarParser.File_inputContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#stmt.
    def visitStmt(self, ctx:GrammarParser.StmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#omp_stmt.
    def visitOmp_stmt(self, ctx:GrammarParser.Omp_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#omp_directive.
    def visitOmp_directive(self, ctx:GrammarParser.Omp_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#parallel_directive.
    def visitParallel_directive(self, ctx:GrammarParser.Parallel_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#parallel_for_directive.
    def visitParallel_for_directive(self, ctx:GrammarParser.Parallel_for_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#for_directive.
    def visitFor_directive(self, ctx:GrammarParser.For_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#parallel_sections_directive.
    def visitParallel_sections_directive(self, ctx:GrammarParser.Parallel_sections_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#sections_directive.
    def visitSections_directive(self, ctx:GrammarParser.Sections_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#section_directive.
    def visitSection_directive(self, ctx:GrammarParser.Section_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#master_directive.
    def visitMaster_directive(self, ctx:GrammarParser.Master_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#single_directive.
    def visitSingle_directive(self, ctx:GrammarParser.Single_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#critical_directive.
    def visitCritical_directive(self, ctx:GrammarParser.Critical_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#barrier_directive.
    def visitBarrier_directive(self, ctx:GrammarParser.Barrier_directiveContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#num_threads.
    def visitNum_threads(self, ctx:GrammarParser.Num_threadsContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#shared.
    def visitShared(self, ctx:GrammarParser.SharedContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#private_.
    def visitPrivate_(self, ctx:GrammarParser.Private_Context):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#first_private.
    def visitFirst_private(self, ctx:GrammarParser.First_privateContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#last_private.
    def visitLast_private(self, ctx:GrammarParser.Last_privateContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#reduction.
    def visitReduction(self, ctx:GrammarParser.ReductionContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#schedule.
    def visitSchedule(self, ctx:GrammarParser.ScheduleContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#for_suite.
    def visitFor_suite(self, ctx:GrammarParser.For_suiteContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#sections_suite.
    def visitSections_suite(self, ctx:GrammarParser.Sections_suiteContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#simple_stmt.
    def visitSimple_stmt(self, ctx:GrammarParser.Simple_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#compound_stmt.
    def visitCompound_stmt(self, ctx:GrammarParser.Compound_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#single_input.
    def visitSingle_input(self, ctx:GrammarParser.Single_inputContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#eval_input.
    def visitEval_input(self, ctx:GrammarParser.Eval_inputContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#decorator.
    def visitDecorator(self, ctx:GrammarParser.DecoratorContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#decorators.
    def visitDecorators(self, ctx:GrammarParser.DecoratorsContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#decorated.
    def visitDecorated(self, ctx:GrammarParser.DecoratedContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#async_funcdef.
    def visitAsync_funcdef(self, ctx:GrammarParser.Async_funcdefContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#funcdef.
    def visitFuncdef(self, ctx:GrammarParser.FuncdefContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#parameters.
    def visitParameters(self, ctx:GrammarParser.ParametersContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#typedargslist.
    def visitTypedargslist(self, ctx:GrammarParser.TypedargslistContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#tfpdef.
    def visitTfpdef(self, ctx:GrammarParser.TfpdefContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#varargslist.
    def visitVarargslist(self, ctx:GrammarParser.VarargslistContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#vfpdef.
    def visitVfpdef(self, ctx:GrammarParser.VfpdefContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#small_stmt.
    def visitSmall_stmt(self, ctx:GrammarParser.Small_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#expr_stmt.
    def visitExpr_stmt(self, ctx:GrammarParser.Expr_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#annassign.
    def visitAnnassign(self, ctx:GrammarParser.AnnassignContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#testlist_star_expr.
    def visitTestlist_star_expr(self, ctx:GrammarParser.Testlist_star_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#augassign.
    def visitAugassign(self, ctx:GrammarParser.AugassignContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#del_stmt.
    def visitDel_stmt(self, ctx:GrammarParser.Del_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#pass_stmt.
    def visitPass_stmt(self, ctx:GrammarParser.Pass_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#flow_stmt.
    def visitFlow_stmt(self, ctx:GrammarParser.Flow_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#break_stmt.
    def visitBreak_stmt(self, ctx:GrammarParser.Break_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#continue_stmt.
    def visitContinue_stmt(self, ctx:GrammarParser.Continue_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#return_stmt.
    def visitReturn_stmt(self, ctx:GrammarParser.Return_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#yield_stmt.
    def visitYield_stmt(self, ctx:GrammarParser.Yield_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#raise_stmt.
    def visitRaise_stmt(self, ctx:GrammarParser.Raise_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#import_stmt.
    def visitImport_stmt(self, ctx:GrammarParser.Import_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#import_name.
    def visitImport_name(self, ctx:GrammarParser.Import_nameContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#import_from.
    def visitImport_from(self, ctx:GrammarParser.Import_fromContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#import_as_name.
    def visitImport_as_name(self, ctx:GrammarParser.Import_as_nameContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#dotted_as_name.
    def visitDotted_as_name(self, ctx:GrammarParser.Dotted_as_nameContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#import_as_names.
    def visitImport_as_names(self, ctx:GrammarParser.Import_as_namesContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#dotted_as_names.
    def visitDotted_as_names(self, ctx:GrammarParser.Dotted_as_namesContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#dotted_name.
    def visitDotted_name(self, ctx:GrammarParser.Dotted_nameContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#global_stmt.
    def visitGlobal_stmt(self, ctx:GrammarParser.Global_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#nonlocal_stmt.
    def visitNonlocal_stmt(self, ctx:GrammarParser.Nonlocal_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#assert_stmt.
    def visitAssert_stmt(self, ctx:GrammarParser.Assert_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#async_stmt.
    def visitAsync_stmt(self, ctx:GrammarParser.Async_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#if_stmt.
    def visitIf_stmt(self, ctx:GrammarParser.If_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#while_stmt.
    def visitWhile_stmt(self, ctx:GrammarParser.While_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#for_stmt.
    def visitFor_stmt(self, ctx:GrammarParser.For_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#try_stmt.
    def visitTry_stmt(self, ctx:GrammarParser.Try_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#with_stmt.
    def visitWith_stmt(self, ctx:GrammarParser.With_stmtContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#with_item.
    def visitWith_item(self, ctx:GrammarParser.With_itemContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#except_clause.
    def visitExcept_clause(self, ctx:GrammarParser.Except_clauseContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#suite.
    def visitSuite(self, ctx:GrammarParser.SuiteContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#test.
    def visitTest(self, ctx:GrammarParser.TestContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#test_nocond.
    def visitTest_nocond(self, ctx:GrammarParser.Test_nocondContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#lambdef.
    def visitLambdef(self, ctx:GrammarParser.LambdefContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#lambdef_nocond.
    def visitLambdef_nocond(self, ctx:GrammarParser.Lambdef_nocondContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#or_test.
    def visitOr_test(self, ctx:GrammarParser.Or_testContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#and_test.
    def visitAnd_test(self, ctx:GrammarParser.And_testContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#not_test.
    def visitNot_test(self, ctx:GrammarParser.Not_testContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#comparison.
    def visitComparison(self, ctx:GrammarParser.ComparisonContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#comp_op.
    def visitComp_op(self, ctx:GrammarParser.Comp_opContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#star_expr.
    def visitStar_expr(self, ctx:GrammarParser.Star_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#expr.
    def visitExpr(self, ctx:GrammarParser.ExprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#xor_expr.
    def visitXor_expr(self, ctx:GrammarParser.Xor_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#and_expr.
    def visitAnd_expr(self, ctx:GrammarParser.And_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#shift_expr.
    def visitShift_expr(self, ctx:GrammarParser.Shift_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#arith_expr.
    def visitArith_expr(self, ctx:GrammarParser.Arith_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#term.
    def visitTerm(self, ctx:GrammarParser.TermContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#factor.
    def visitFactor(self, ctx:GrammarParser.FactorContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#power.
    def visitPower(self, ctx:GrammarParser.PowerContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#atom_expr.
    def visitAtom_expr(self, ctx:GrammarParser.Atom_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#atom.
    def visitAtom(self, ctx:GrammarParser.AtomContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#testlist_comp.
    def visitTestlist_comp(self, ctx:GrammarParser.Testlist_compContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#trailer.
    def visitTrailer(self, ctx:GrammarParser.TrailerContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#subscriptlist.
    def visitSubscriptlist(self, ctx:GrammarParser.SubscriptlistContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#subscript.
    def visitSubscript(self, ctx:GrammarParser.SubscriptContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#sliceop.
    def visitSliceop(self, ctx:GrammarParser.SliceopContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#exprlist.
    def visitExprlist(self, ctx:GrammarParser.ExprlistContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#testlist.
    def visitTestlist(self, ctx:GrammarParser.TestlistContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#dictorsetmaker.
    def visitDictorsetmaker(self, ctx:GrammarParser.DictorsetmakerContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#classdef.
    def visitClassdef(self, ctx:GrammarParser.ClassdefContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#arglist.
    def visitArglist(self, ctx:GrammarParser.ArglistContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#argument.
    def visitArgument(self, ctx:GrammarParser.ArgumentContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#comp_iter.
    def visitComp_iter(self, ctx:GrammarParser.Comp_iterContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#comp_for.
    def visitComp_for(self, ctx:GrammarParser.Comp_forContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#comp_if.
    def visitComp_if(self, ctx:GrammarParser.Comp_ifContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#encoding_decl.
    def visitEncoding_decl(self, ctx:GrammarParser.Encoding_declContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#yield_expr.
    def visitYield_expr(self, ctx:GrammarParser.Yield_exprContext):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by GrammarParser#yield_arg.
    def visitYield_arg(self, ctx:GrammarParser.Yield_argContext):
        return self.visitChildren(ctx)



del GrammarParser