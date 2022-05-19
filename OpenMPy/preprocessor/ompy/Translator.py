from ompy.antlr_generated.GrammarVisitor import *
from ompy.antlr_generated.GrammarParser import *
from antlr4 import TerminalNode
import os


class Translator(GrammarVisitor):

    def __init__(self, printer):
        self.printer = printer
        self.incrementing_target_id = 0
        #self.nesting_level = 0

    # Visit a parse tree produced by GrammarParser#file_input.
    def visitFile_input(self, ctx: GrammarParser.File_inputContext):
        #self.printer.println('#jython program')
        self.printer.println('import sys')
        #self.printer.println('sys.path.append(\'C:\\\\Users\\Caleb\\\\PycharmProjects\\\\OpenMPy\\\\ompy\')')
        #self.printer.println('sys.path.append(\'/Users/calebhuck/PycharmProjects/OpenMPy/ompy/\')')
        #self.printer.println('sys.path.append(\'/home/pi/PycharmProjects/OpenMPy/ompy\')')

        self.printer.println('from jarray import array, zeros')
        #self.printer.println('from omp import *')
        self.printer.println('from runtime import *')
        #self.printer.println('try:')
        #self.printer.indent()
        self.printer.println('from Queue import Queue')
        #self.printer.dedent()
        #self.printer.println('except ModuleNotFoundError:')
        #self.printer.indent()
        #self.printer.println('from queue import Queue')
        #self.printer.dedent()
        self.printer.println('from threading import current_thread\n\n')
        self.printer.println('_num_threads_ = 1')
        self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#parallel_directive.
    def visitParallel_directive(self, ctx: GrammarParser.Parallel_directiveContext):
        #self.nesting_level += 1
        if ctx.num_threads() != []:
            if len(ctx.num_threads()) != 1:
                raise Exception('parallel for directive can only have one num_threads() clause')
            self.visitNum_threads_clause(ctx.num_threads(0))
        else:
            # could also add environment variable support in future
            self.printer.println('_num_threads_ = {}'.format(os.cpu_count()))

        target_name = '_target_' + str(self.incrementing_target_id)
        #manager_name = '_manager_{}_'.format(self.incrementing_target_id)
        self.incrementing_target_id += 1
        self.printer.println('def {}(_manager_):'.format(target_name))

        reductions = []
        if ctx.reduction() != []:
            for item in ctx.reduction():
                self.printer.indent()
                reductions.append(self.visitReduction(item))
                self.printer.dedent()

        if ctx.shared() != []:
            for item in ctx.shared():
                self.printer.indent()
                self.visitShared(item)
                self.printer.dedent()

        if ctx.private_()  != []:
            for item in ctx.private_():
                self.printer.indent()
                self.visitPrivate_(item)
                self.printer.dedent()

        self.visitSuite(ctx.suite())
        if reductions:
            self.printer.indent()
            for group in reductions:
                # self.printer.println('{} = _manager_.get_reduction_value(\'{}\')'.format(var, var))
                for key in group.keys():
                    for var in group[key]:
                        self.printer.println('_manager_.update_reduction_variable(\'{}\', {}, \'{}\')'.format(var, var, key))
            self.printer.dedent()

        self.printer.println('_manager_outer_ = RuntimeManager(_num_threads_)')#.format(self.nesting_level))
        self.printer.println('submit({}, _num_threads_, args=(_manager_outer_,))'.format(target_name))

        if reductions:
            for group in reductions:
                #self.printer.println('{} = _manager_.get_reduction_value(\'{}\')'.format(var, var))
                for key in group.keys():
                    for var in group[key]:
                        self.printer.println('{} = _manager_outer_.get_reduction_value(\'{}\')'.format(var, var))
        #self.nesting_level -= 1

    # Visit a parse tree produced by GrammarParser#parallel_for_directive.
    def visitParallel_for_directive(self, ctx: GrammarParser.Parallel_for_directiveContext):
        if ctx.num_threads() != []:
            if len(ctx.num_threads()) > 1:
                raise Exception('parallel for directive can only have one num_threads() clause')
            self.visitNum_threads_clause(ctx.num_threads(0))
        else:
            # could also add environment variable support in future
            self.printer.println('_num_threads_ = {}'.format(os.cpu_count()))

        schedule, chunk, args = None, None, []
        if ctx.schedule() != []:
            if len(ctx.schedule()) != 1:
                raise Exception('parallel for directive can only have one schedule() clause')
            schedule, chunk = self.visitSchedule(ctx.schedule(0))
        if schedule is not None:
            self.printer.println('_schedule_ = \'{}\''.format(schedule))
        else:
            self.printer.println('_schedule_ = {}'.format(schedule))
        self.printer.println('_chunk_ = {}'.format(chunk))
        # self.printer.println('for_manager = ForManager(schedule, chunk, num_threads)')

        # self.printer.println('def {}(for_manager):'.format(target_name))
        target_name = '_target_' + str(self.incrementing_target_id)
        self.incrementing_target_id += 1
        self.printer.println('def {}(_manager_):'.format(target_name))
        self.printer.indent()

        reductions = []
        if ctx.reduction() != []:
            for item in ctx.reduction():
                #self.printer.indent()
                reductions.append(self.visitReduction(item))
                #self.printer.dedent()

        if ctx.shared() != []:
            for item in ctx.shared():
                #self.printer.indent()
                self.visitShared(item)
                #self.printer.dedent()

        if ctx.private_()  != []:
            for item in ctx.private_():
                #self.printer.indent()
                self.visitPrivate_(item)
                #self.printer.dedent()

        self.visitFor_suite(ctx.for_suite())

        if reductions:
            self.printer.indent()
            for group in reductions:
                # self.printer.println('{} = _manager_.get_reduction_value(\'{}\')'.format(var, var))
                for key in group.keys():
                    for var in group[key]:
                        self.printer.println('_manager_.update_reduction_variable(\'{}\', {}, \'{}\')'.format(var, var, key))
            self.printer.dedent()

        #self.printer.println('_comm_q_ = Queue()')
        self.printer.println('_for_manager_ = ForManager(_schedule_, _chunk_, _num_threads_)')
        self.printer.println('_manager_ = RuntimeManager(_num_threads_)')
        self.printer.println('_manager_.set_for(_for_manager_)')
        self.printer.println('submit({}, _num_threads_, args=(_manager_,))'.format(target_name))

        if reductions:
            for group in reductions:
                #self.printer.println('{} = _manager_.get_reduction_value(\'{}\')'.format(var, var))
                for key in group.keys():
                    for var in group[key]:
                        self.printer.println('{} = _manager_.get_reduction_value(\'{}\')'.format(var, var))

    # Visit a parse tree produced by GrammarParser#for_directive.
    def visitFor_directive(self, ctx: GrammarParser.For_directiveContext):
        schedule, chunk, args = None, None, []
        if ctx.schedule() is not None:
            schedule, chunk = self.visitSchedule(ctx.schedule())
        if schedule is not None:
            self.printer.println('_schedule_ = \'{}\''.format(schedule))
        else:
            self.printer.println('_schedule_ = {}'.format(schedule))
        self.printer.println('_chunk_ = {}'.format(chunk))
        # self.printer.println('global num_threads')

        self.printer.println('if omp_get_thread_num() == 0:')
        self.printer.indent()
        self.printer.println('_for_manager_ = ForManager(_schedule_, _chunk_, _num_threads_)')
        #self.printer.println('_comm_q_.put(_for_manager_)')
        self.printer.println('_manager_.set_for(_for_manager_)')
        self.printer.dedent()
        self.printer.println('else:')
        self.printer.indent()
        self.printer.println('_for_manager_ = _manager_.get_for()')
        #self.printer.println('_comm_q_.put(_for_manager_)')
        self.printer.dedent()
        self.visit(ctx.for_suite())

    # Visit a parse tree produced by GrammarParser#for_suite.
    def visitFor_suite(self, ctx: GrammarParser.For_suiteContext):
        if isinstance(ctx.parentCtx, GrammarParser.Parallel_for_directiveContext):
            '''self.printer.println('global schedule')
            self.printer.println('global chunk')
            self.printer.println('global num_threads')'''
            args = []
            for arg in ctx.argument():
                args.append(self.visitArgument(arg))
            for i in range(3):
                try:
                    self.printer.println('_arg{}_ = {}'.format(i + 1, args[i]))
                except IndexError:
                    self.printer.println('_arg{}_ = None'.format(i + 1))

            self.printer.println('_for_manager_.setup(_arg1_, _arg2_, _arg3_)')

            self.printer.println('while True:')
            self.printer.indent()
            self.printer.println('_start_, _stop_, _step_ = _for_manager_.request()')
            self.printer.println('if _start_ == _stop_: break')
            self.printer.print('for {} in range(_start_, _stop_, _step_):'.format(ctx.NAME(0)))
            self.visitSuite(ctx.suite())
            #self.printer.println('if _for_manager_.done(): break')
            self.printer.dedent()
            self.printer.dedent()
            # return args
        else:
            args = []
            for arg in ctx.argument():
                args.append(self.visitArgument(arg))
            for i in range(3):
                try:
                    self.printer.println('_arg{}_ = {}'.format(i + 1, args[i]))
                except IndexError:
                    self.printer.println('_arg{}_ = None'.format(i + 1))

            self.printer.println('_for_manager_.setup(_arg1_, _arg2_, _arg3_)')
            self.printer.println('while True:')
            self.printer.indent()
            self.printer.println('_start_, _stop_, _step_ = _for_manager_.request()')
            self.printer.print('for {} in range(_start_, _stop_, _step_):'.format(ctx.NAME(0)))
            self.visitSuite(ctx.suite())
            self.printer.println('if _for_manager_.done(): break')
            self.printer.dedent()
            # return args

    # Visit a parse tree produced by GrammarParser#reduction.
    def visitReduction(self, ctx:GrammarParser.ReductionContext):
        operator = ctx.getChild(1).getSymbol().text

        for name in ctx.NAME():
            self.printer.println('{} = 0'.format(name))
            #self.printer.println('_manager_.reduce({}, {})'.format(operator, name))
        #return ctx.NAME()
        reduce = {operator: []}
        for name in ctx.NAME():
            reduce[operator].append(name.getSymbol().text)
        return reduce

    # Visit a parse tree produced by GrammarParser#schedule.
    def visitSchedule(self, ctx: GrammarParser.ScheduleContext):
        schedule = ctx.SCHEDULE().getSymbol().text
        chunk = None
        if ctx.NUMBER() is not None:
            try:
                chunk = int(ctx.NUMBER().getSymbol().text)
            except ValueError:
                print('Error: schedule clause chunk value must be an integer, {} provided'.format(type(chunk)))
        else:
            chunk = None
        return schedule, chunk

    # Visit a parse tree produced by GrammarParser#shared.
    def visitShared(self, ctx: GrammarParser.SharedContext):
        count = ctx.getChildCount()
        if count == 1:
            self.printer.println('global {}'.format(ctx.NAME().getSymbol().text))
        else:
            for name in ctx.NAME():
                self.printer.println('global {}'.format(name.getSymbol().text))

    # Visit a parse tree produced by GrammarParser#private_.
    def visitPrivate_(self, ctx: GrammarParser.Private_Context):
        count = ctx.getChildCount()
        if count == 1:
            print('count is one')
            self.printer.println('{} = 0'.format(ctx.NAME().getSymbol().text))
        else:
            for name in ctx.NAME():
                self.printer.println('{} = 0'.format(name.getSymbol().text))

    # Visit a parse tree produced by GrammarParser#omp_stmt.
    def visitOmp_stmt(self, ctx: GrammarParser.Omp_stmtContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#omp_directive.
    def visitOmp_directive(self, ctx: GrammarParser.Omp_directiveContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#sections_directive.
    def visitSections_directive(self, ctx: GrammarParser.Sections_directiveContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#section_directive.
    def visitSection_directive(self, ctx: GrammarParser.Section_directiveContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#master_directive.
    def visitMaster_directive(self, ctx: GrammarParser.Master_directiveContext):
        self.printer.println('if get_current_thread_id() == 0:')
        self.visitSuite(ctx.suite())

    # Visit a parse tree produced by GrammarParser#single_directive.
    def visitSingle_directive(self, ctx: GrammarParser.Single_directiveContext):
        self.printer.println('if get_current_thread_id() == 0:')
        self.visitSuite(ctx.suite())

    # Visit a parse tree produced by GrammarParser#critical_directive.
    def visitCritical_directive(self, ctx: GrammarParser.Critical_directiveContext):
        self.printer.println('_manager_.critical_lock.acquire()')
        self.printer.dedent()
        self.visitSuite(ctx.suite())
        self.printer.indent()
        self.printer.println('_manager_.critical_lock.release()')


    # Visit a parse tree produced by GrammarParser#barrier_directive.
    def visitBarrier_directive(self, ctx: GrammarParser.Barrier_directiveContext):
        self.printer.println('_manager_.barrier()')

    # Visit a parse tree produced by GrammarParser#num_threads_clause.
    def visitNum_threads_clause(self, ctx: GrammarParser.Num_threadsContext):
        self.printer.println('_num_threads_ = {}'.format(self.visit(ctx.argument())))

    # Visit a parse tree produced by GrammarParser#for_stmt.
    def visitFor_stmt(self, ctx: GrammarParser.For_stmtContext):
        str = ''
        str += 'for '
        str += self.visitExprlist(ctx.exprlist())
        str += ' in '
        str += self.visitTestlist(ctx.testlist())
        str += ':'
        if isinstance(ctx.parentCtx, GrammarParser.Async_stmtContext):
            self.printer.print('async ' + str)
            self.visitSuite(ctx.suite(0))
        else:
            self.printer.print(str)
            self.visitSuite(ctx.suite(0))
        if ctx.ELSE() is not None:
            self.printer.print('else:')
            self.visit(ctx.suite(1))

    # Visit a parse tree produced by GrammarParser#exprlist.
    def visitExprlist(self, ctx: GrammarParser.ExprlistContext):
        str = ''
        count = ctx.getChildCount()
        for i in range(count):
            if not isinstance(ctx.getChild(i), TerminalNode):
                str += self.visit(ctx.getChild(i))
            else:
                str += ', '
        if count % 2 == 0:
            str += ' '
        return str

    # Visit a parse tree produced by GrammarParser#expr.
    def visitExpr(self, ctx: GrammarParser.ExprContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitXor_expr(ctx.xor_expr(0))
            for i in range(len(ctx.num_or)):
                str += ' | '
                str += self.visitXor_expr(ctx.xor_expr(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#xor_expr.
    def visitXor_expr(self, ctx: GrammarParser.Xor_exprContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitAnd_expr(ctx.and_expr(0))
            for i in range(len(ctx.num_xor)):
                str += ' ^ '
                str += self.visitAnd_expr(ctx.and_expr(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#stmt.
    def visitStmt(self, ctx: GrammarParser.StmtContext):
        self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#simple_stmt.
    def visitSimple_stmt(self, ctx: GrammarParser.Simple_stmtContext):
        count = ctx.getChildCount()
        stmt_count = 0
        str = ''
        for i in range(count - 1):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += '; '
            else:
                str += self.visitSmall_stmt(ctx.small_stmt(stmt_count))
                stmt_count += 1
        self.printer.print(str)
        self.printer.newline()

    # Visit a parse tree produced by GrammarParser#compound_stmt.
    def visitCompound_stmt(self, ctx: GrammarParser.Compound_stmtContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#single_input.
    def visitSingle_input(self, ctx: GrammarParser.Single_inputContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#eval_input.
    def visitEval_input(self, ctx: GrammarParser.Eval_inputContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#decorator.
    def visitDecorator(self, ctx: GrammarParser.DecoratorContext):
        str = '@' + self.visit(ctx.dotted_name())
        if ctx.OPEN_PAREN() is not None:
            str += '('
            if ctx.arglist() is not None:
                str += self.visitArglist(ctx.arglist())
            str += ')'
        self.printer.print(str)
        self.printer.newline()

    # Visit a parse tree produced by GrammarParser#decorators.
    def visitDecorators(self, ctx: GrammarParser.DecoratorsContext):
        for i in range(ctx.getChildCount()):
            self.visit(ctx.getChild(i))

    # Visit a parse tree produced by GrammarParser#decorated.
    def visitDecorated(self, ctx: GrammarParser.DecoratedContext):
        str = self.visitDecorators(ctx.decorators())
        self.visit(ctx.getChild(1))
        return str

    # Visit a parse tree produced by GrammarParser#async_funcdef.
    def visitAsync_funcdef(self, ctx: GrammarParser.Async_funcdefContext):
        self.visitFuncdef(ctx.funcdef())

    # Visit a parse tree produced by GrammarParser#funcdef.
    def visitFuncdef(self, ctx: GrammarParser.FuncdefContext):
        str = 'def ' + ctx.NAME().getSymbol().text + self.visitParameters(ctx.parameters())
        if ctx.ARROW() is not None:
            str += ' -> ' + self.visitTest(ctx.test())
        str += ':'
        if isinstance(ctx.parentCtx, GrammarParser.Async_stmtContext) or \
                isinstance(ctx.parentCtx, GrammarParser.Async_funcdefContext):
            self.printer.print('async ' + str)
            self.visitSuite(ctx.suite())
        else:
            self.printer.print(str)
            self.visitSuite(ctx.suite())

    # Visit a parse tree produced by GrammarParser#parameters.
    def visitParameters(self, ctx: GrammarParser.ParametersContext):
        str = '('
        if ctx.typedargslist() is not None:
            str += self.visit(ctx.getChild(1))
        str += ')'
        return str

    # Visit a parse tree produced by GrammarParser#typedargslist.
    def visitTypedargslist(self, ctx: GrammarParser.TypedargslistContext):
        count = ctx.getChildCount()
        str = ''
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                txt = ctx.getChild(i).getSymbol().text
                if txt == ',':
                    str += ', '
                elif txt == '=':
                    str += ' = '
                elif txt == '*':
                    str += '*'
                elif txt == '**':
                    str += '**'
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#tfpdef.
    def visitTfpdef(self, ctx: GrammarParser.TfpdefContext):
        count = ctx.getChildCount()
        if count == 1:
            return ctx.NAME().getSymbol().text
        else:
            return ctx.NAME().getSymbol().text + ': ' + self.visitTest(ctx.test())

    # Visit a parse tree produced by GrammarParser#varargslist.
    def visitVarargslist(self, ctx: GrammarParser.VarargslistContext):
        count = ctx.getChildCount()
        str = ''
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                txt = ctx.getChild(i).getSymbol().text
                if txt == ',':
                    str += ', '
                elif txt == '=':
                    str += ' = '
                elif txt == '*':
                    str += '*'
                elif txt == '**':
                    str += '**'
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#vfpdef.
    def visitVfpdef(self, ctx: GrammarParser.VfpdefContext):
        return ctx.getChild(0).getSymbol().text

    # Visit a parse tree produced by GrammarParser#small_stmt.
    def visitSmall_stmt(self, ctx: GrammarParser.Small_stmtContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#expr_stmt.
    def visitExpr_stmt(self, ctx: GrammarParser.Expr_stmtContext):
        count = ctx.getChildCount()
        str = ''
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += '='
            else:
                str += self.visit(ctx.getChild(i))
            if i != count - 1:
                str += ' '
        return str

    # Visit a parse tree produced by GrammarParser#annassign.
    def visitAnnassign(self, ctx: GrammarParser.AnnassignContext):
        str = ': ' + self.visit(ctx.getChild(1))
        if ctx.getChildCount() == 4:
            str += ' = ' + self.visit(ctx.getChild(3))
        return str

    # Visit a parse tree produced by GrammarParser#testlist_star_expr.
    def visitTestlist_star_expr(self, ctx: GrammarParser.Testlist_star_exprContext):
        count = ctx.getChildCount()
        str = ''
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += ', '
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#augassign.
    def visitAugassign(self, ctx: GrammarParser.AugassignContext):
        return ctx.getChild(0).getSymbol().text

    # Visit a parse tree produced by GrammarParser#del_stmt.
    def visitDel_stmt(self, ctx: GrammarParser.Del_stmtContext):
        str = 'del '
        str += self.visitExprlist(ctx.exprlist())
        return str

    # Visit a parse tree produced by GrammarParser#pass_stmt.
    def visitPass_stmt(self, ctx: GrammarParser.Pass_stmtContext):
        return 'pass'

    # Visit a parse tree produced by GrammarParser#flow_stmt.
    def visitFlow_stmt(self, ctx: GrammarParser.Flow_stmtContext):
        return self.visit(ctx.getChild(0))

    # Visit a parse tree produced by GrammarParser#break_stmt.
    def visitBreak_stmt(self, ctx: GrammarParser.Break_stmtContext):
        return 'break'

    # Visit a parse tree produced by GrammarParser#continue_stmt.
    def visitContinue_stmt(self, ctx: GrammarParser.Continue_stmtContext):
        return 'continue'

    # Visit a parse tree produced by GrammarParser#return_stmt.
    def visitReturn_stmt(self, ctx: GrammarParser.Return_stmtContext):
        count = ctx.getChildCount()
        if count == 1:
            return 'return'
        else:
            return 'return ' + self.visit(ctx.getChild(1))

    # Visit a parse tree produced by GrammarParser#yield_stmt.
    def visitYield_stmt(self, ctx: GrammarParser.Yield_stmtContext):
        return self.visit(ctx.getChild(0))

    # Visit a parse tree produced by GrammarParser#raise_stmt.
    def visitRaise_stmt(self, ctx: GrammarParser.Raise_stmtContext):
        count = ctx.getChildCount()
        if count == 1:
            return 'raise'
        elif count == 2:
            return 'raise ' + self.visit(ctx.getChild(1))
        else:
            return 'raise ' + self.visit(ctx.getChild(1)) + ' from ' + self.visit(ctx.getChild(3))

    # Visit a parse tree produced by GrammarParser#import_stmt.
    def visitImport_stmt(self, ctx: GrammarParser.Import_stmtContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#import_name.
    def visitImport_name(self, ctx: GrammarParser.Import_nameContext):
        return 'import ' + self.visit(ctx.getChild(1))

    # Visit a parse tree produced by GrammarParser#import_from.
    def visitImport_from(self, ctx: GrammarParser.Import_fromContext):
        str = 'from '
        count = ctx.getChildCount()
        for i in range(1, count):
            if isinstance(ctx.getChild(i), TerminalNode):
                txt = ctx.getChild(i).getSymbol().text
                if txt == 'import':
                    str += ' import '
                else:
                    str += txt
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#import_as_name.
    def visitImport_as_name(self, ctx: GrammarParser.Import_as_nameContext):
        count = ctx.getChildCount()
        if count == 1:
            return ctx.getChild(0).getSymbol().text
        else:
            return ctx.getChild(0).getSymbol().text + ' as ' + ctx.getChild(2).getSymbol().text

    # Visit a parse tree produced by GrammarParser#dotted_as_name.
    def visitDotted_as_name(self, ctx: GrammarParser.Dotted_as_nameContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visit(ctx.getChild(0))
        else:
            return self.visit(ctx.getChild(0)) + ' as ' + ctx.NAME().getSymbol().text

    # Visit a parse tree produced by GrammarParser#import_as_names.
    def visitImport_as_names(self, ctx: GrammarParser.Import_as_namesContext):
        str = ''
        count = ctx.getChildCount()
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += ', '
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#dotted_as_names.
    def visitDotted_as_names(self, ctx: GrammarParser.Dotted_as_namesContext):
        str = ''
        count = ctx.getChildCount()
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += ', '
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#dotted_name.
    def visitDotted_name(self, ctx: GrammarParser.Dotted_nameContext):
        str = ''
        for i in range(ctx.getChildCount()):
            str += ctx.getChild(i).getSymbol().text
        return str

    # Visit a parse tree produced by GrammarParser#global_stmt.
    def visitGlobal_stmt(self, ctx: GrammarParser.Global_stmtContext):
        str = 'global '
        for i in range(1, ctx.getChildCount()):
            if ctx.getChild(i).getSymbol().text == ',':
                str += ', '
            else:
                str += ctx.getChild(i).getSymbol().text
        return str

    # Visit a parse tree produced by GrammarParser#nonlocal_stmt.
    def visitNonlocal_stmt(self, ctx: GrammarParser.Nonlocal_stmtContext):
        str = 'nonlocal '
        for i in range(1, ctx.getChildCount()):
            if ctx.getChild(i).getSymbol().text == ',':
                str += ', '
            else:
                str += ctx.getChild(i).getSymbol().text
        return str

    # Visit a parse tree produced by GrammarParser#assert_stmt.
    def visitAssert_stmt(self, ctx: GrammarParser.Assert_stmtContext):
        str = 'assert '
        for i in range(1, ctx.getChildCount()):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += ', '
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#async_stmt.
    def visitAsync_stmt(self, ctx: GrammarParser.Async_stmtContext):
        self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#if_stmt.
    def visitIf_stmt(self, ctx: GrammarParser.If_stmtContext):
        str = ''
        str += 'if '
        str += self.visitTest(ctx.test(0))
        str += ':'
        self.printer.print(str)
        self.visitSuite(ctx.suite(0))
        for i in range(len(ctx.ELIF())):
            str = ''
            str += 'elif '
            str += self.visitTest(ctx.test(i + 1))
            str += ':'
            self.printer.print(str)
            self.visitSuite(ctx.suite(i + 1))
        if ctx.ELSE() is not None:
            self.printer.print('else:')
            self.visitSuite(ctx.suite(len(ctx.num_suite) - 1))

    # Visit a parse tree produced by GrammarParser#while_stmt.
    def visitWhile_stmt(self, ctx: GrammarParser.While_stmtContext):
        str = ''
        str += 'while '
        str += self.visitTest(ctx.test())
        str += ':'
        self.printer.print(str)
        self.visitSuite(ctx.suite(0))
        if ctx.getChildCount() == 7:
            str += 'else:'
            self.visitSuite(ctx.suite(1))

    # Visit a parse tree produced by GrammarParser#try_stmt.
    def visitTry_stmt(self, ctx: GrammarParser.Try_stmtContext):
        self.printer.print('try:')
        self.visitSuite(ctx.suite(0))
        suite_counter = 1
        if ctx.except_clause() != []:
            for clause in ctx.except_clause():
                self.printer.print(self.visitExcept_clause(clause) + ':')
                self.visitSuite(ctx.suite(suite_counter))
                suite_counter += 1
            if ctx.ELSE() is not None:
                self.printer.print('else:')
                self.visitSuite(ctx.suite(suite_counter))
                suite_counter += 1
            if ctx.FINALLY() is not None:
                self.printer.print('finally:')
                self.visitSuite(ctx.suite(suite_counter))
        else:
            self.printer.print('finally:')
            self.visitSuite(ctx.suite(suite_counter))

    # Visit a parse tree produced by GrammarParser#with_stmt.
    def visitWith_stmt(self, ctx: GrammarParser.With_stmtContext):
        str = 'with '
        num_with_items = len(ctx.with_item())
        for i in range(num_with_items):
            str += self.visitWith_item(ctx.with_item(i))
            if i != num_with_items - 1:
                str += ', '
        str += ':'
        if isinstance(ctx.parentCtx, GrammarParser.Async_stmtContext):
            self.printer.print('async ' + str)
            self.visitSuite(ctx.suite())
        else:
            self.printer.print(str)
            self.visitSuite(ctx.suite())

    # Visit a parse tree produced by GrammarParser#with_item.
    def visitWith_item(self, ctx: GrammarParser.With_itemContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visit(ctx.getChild(0))
        else:
            return self.visit(ctx.getChild(0)) + ' as ' + self.visit(ctx.getChild(2))

    # Visit a parse tree produced by GrammarParser#except_clause.
    def visitExcept_clause(self, ctx: GrammarParser.Except_clauseContext):
        count = ctx.getChildCount()
        if count == 1:
            return 'except'
        elif count == 2:
            return 'except ' + self.visit(ctx.getChild(1))
        else:
            return 'except ' + self.visit(ctx.getChild(1)) + ' as ' + ctx.getChild(3).getSymbol().text

    # Visit a parse tree produced by GrammarParser#suite.
    def visitSuite(self, ctx: GrammarParser.SuiteContext):
        count = ctx.getChildCount()
        if count == 1:
            self.visitChildren(ctx)
        else:
            self.printer.newline()
            self.printer.indent()
            for i in range(len(ctx.num_stmt)):
                self.visitStmt(ctx.stmt(i))
            self.printer.dedent()

    # Visit a parse tree produced by GrammarParser#test.
    def visitTest(self, ctx: GrammarParser.TestContext):
        count = ctx.getChildCount()
        # print(ctx.getChild(0).getSymbol().text, type(ctx.getChild(1)))
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitOr_test(ctx.or_test(0))
            str += ' if '
            str += self.visitOr_test(ctx.or_test(1))
            str += ' else '
            str += self.visitTest(ctx.test())
            return str

    # Visit a parse tree produced by GrammarParser#test_nocond.
    def visitTest_nocond(self, ctx: GrammarParser.Test_nocondContext):
        return self.visit(ctx.getChild(0))

    # Visit a parse tree produced by GrammarParser#lambdef.
    def visitLambdef(self, ctx: GrammarParser.LambdefContext):
        str = 'lambda '
        if ctx.varargslist() is not None:
            str += self.visitVarargslist(ctx.varargslist())
        str += ': '
        str += self.visitTest(ctx.test())
        return str

    # Visit a parse tree produced by GrammarParser#lambdef_nocond.
    def visitLambdef_nocond(self, ctx: GrammarParser.Lambdef_nocondContext):
        str = 'lambda '
        if ctx.varargslist() is not None:
            str += self.visitVarargslist(ctx.varargslist())
        str += ': '
        str += self.visitTest(ctx.test_nocond())
        return str

    # Visit a parse tree produced by GrammarParser#or_test.
    def visitOr_test(self, ctx: GrammarParser.Or_testContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitAnd_test(ctx.and_test(0))
            for i in range(len(ctx.num_or)):
                str += ' or '
                str += self.visitAnd_test(ctx.and_test(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#and_test.
    def visitAnd_test(self, ctx: GrammarParser.And_testContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitNot_test(ctx.not_test(0))
            for i in range(len(ctx.num_and)):
                str += ' and '
                str += self.visitNot_test(ctx.not_test(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#not_test.
    def visitNot_test(self, ctx: GrammarParser.Not_testContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += ' not '
            str += self.visitNot_test(ctx.not_test())
            return str

    # Visit a parse tree produced by GrammarParser#comparison.
    def visitComparison(self, ctx: GrammarParser.ComparisonContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitExpr(ctx.expr(0))
            for i in range(len(ctx.num_comp)):
                str += self.visitComp_op(ctx.comp_op(i))
                str += self.visitExpr(ctx.expr(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#comp_op.
    def visitComp_op(self, ctx: GrammarParser.Comp_opContext):
        count = ctx.getChildCount()
        if count == 1:
            return ' ' + ctx.getChild(0).getSymbol().text + ' '
        else:
            return ' ' + ctx.getChild(0).getSymbol().text + ' ' + ctx.getChild(1).getSymbol().text + ' '

    # Visit a parse tree produced by GrammarParser#star_expr.
    def visitStar_expr(self, ctx: GrammarParser.Star_exprContext):
        return '*' + self.visit(ctx.getChild(1))

    # Visit a parse tree produced by GrammarParser#and_expr.
    def visitAnd_expr(self, ctx: GrammarParser.And_exprContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitShift_expr(ctx.shift_expr(0))
            for i in range(len(ctx.num_and)):
                str += ' & '
                str += self.visitShift_expr(ctx.shift_expr(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#shift_expr.
    def visitShift_expr(self, ctx: GrammarParser.Shift_exprContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitArith_expr(ctx.arith_expr(0))
            for i in range(len(ctx.num_shift)):
                str += ' ' + ctx.getChild(i * 2 + 1).getSymbol().text + ' '
                str += self.visitArith_expr(ctx.arith_expr(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#arith_expr.
    def visitArith_expr(self, ctx: GrammarParser.Arith_exprContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            str += self.visitTerm(ctx.term(0))
            for i in range(len(ctx.num_arith)):
                str += ' ' + ctx.getChild(i * 2 + 1).getSymbol().text + ' '
                str += self.visitTerm(ctx.term(i + 1))
            return str

    # Visit a parse tree produced by GrammarParser#term.
    def visitTerm(self, ctx: GrammarParser.TermContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            str = ''
            for i in range(count):
                if isinstance(ctx.getChild(i), TerminalNode):
                    str += ' ' + ctx.getChild(i).getSymbol().text + ' '
                else:
                    str += self.visit(ctx.getChild(i))
            return str

    # Visit a parse tree produced by GrammarParser#factor.
    def visitFactor(self, ctx: GrammarParser.FactorContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visit(ctx.getChild(0))
        else:
            str = ''
            str += ctx.getChild(0).getSymbol().text + ' '
            str += self.visitFactor(ctx.factor())
        return str

    # Visit a parse tree produced by GrammarParser#power.
    def visitPower(self, ctx: GrammarParser.PowerContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitChildren(ctx)
        else:
            return self.visitAtom_expr(ctx.atom_expr()) + ' ** ' + self.visitFactor(ctx.factor())

    # Visit a parse tree produced by GrammarParser#atom_expr.
    def visitAtom_expr(self, ctx: GrammarParser.Atom_exprContext):
        str = ''
        if ctx.AWAIT() is not None:
            str += 'await '
        str += self.visitAtom(ctx.atom())

        if ctx.atom().NAME() is not None:
            if ctx.atom().NAME().getSymbol().text == 'print':
                if ctx.trailer() is not None:
                    if ctx.trailer(0).arglist() is not None:
                        str += ' ' + self.visitArglist(ctx.trailer(0).arglist())
            else:
                for i in range(len(ctx.num_trailer)):
                    str += self.visitTrailer(ctx.trailer(i))
        return str

    # Visit a parse tree produced by GrammarParser#atom.
    def visitAtom(self, ctx: GrammarParser.AtomContext):
        count = ctx.getChildCount()
        str = ''
        if ctx.STRING() is not None:
            for i in range(len(ctx.STRING())):
                str += ctx.STRING()[i].getSymbol().text
                return str
        if count == 1:
            return ctx.getChild(0).getSymbol().text
        elif count == 3:
            if ctx.OPEN_PAREN() is not None:
                str += '('
                if ctx.yield_expr() is not None:
                    str += self.visitYield_expr(ctx.yield_expr())
                elif ctx.testlist_comp() is not None:
                    str += self.visitTestlist_comp(ctx.testlist_comp())
                str += ')'
            elif ctx.OPEN_BRACK() is not None:
                str += '['
                str += self.visitTestlist_comp(ctx.testlist_comp())
                str += ']'
            elif ctx.OPEN_BRACE() is not None:
                str += '{'
                str += self.visitDictorsetmaker(ctx.dictorsetmaker())
                str += '}'
        elif count == 2:
            if ctx.OPEN_PAREN() is not None:
                str += '()'
            elif ctx.OPEN_BRACK() is not None:
                str += '[]'
            elif ctx.OPEN_BRACE() is not None:
                str += '{}'
        else:
            print('VisitAtom: this should never happen')
        return str

    # Visit a parse tree produced by GrammarParser#testlist_comp.
    def visitTestlist_comp(self, ctx: GrammarParser.Testlist_compContext):
        count = ctx.getChildCount()
        str = ''
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += ', '
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#trailer.
    def visitTrailer(self, ctx: GrammarParser.TrailerContext):
        str = ''
        if ctx.OPEN_PAREN() is not None:
            str += '('
            if ctx.arglist() is not None:
                str += self.visitArglist(ctx.arglist())
            str += ')'

        elif ctx.OPEN_BRACK() is not None:
            str += '['
            str += self.visitSubscriptlist(ctx.subscriptlist())
            str += ']'

        else:
            str += '.' + ctx.NAME().getSymbol().text
        return str

    # Visit a parse tree produced by GrammarParser#subscriptlist.
    def visitSubscriptlist(self, ctx: GrammarParser.SubscriptlistContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visitSubscript(ctx.subscript(0))
        else:
            str = ''
            for sub in ctx.subscript():
                str += ', ' + self.visitSubscript(sub)
            if isinstance(ctx.getChild(count - 1), TerminalNode):
                str += ','
            return str


    # Visit a parse tree produced by GrammarParser#subscript.
    def visitSubscript(self, ctx: GrammarParser.SubscriptContext):
        count = ctx.getChildCount()
        if count == 1:
            if isinstance(ctx.getChild(0), TerminalNode):
                return ':'
            else:
                return self.visitTest(ctx.test(0))
        else:
            str = ''
            for i in range(count):
                if isinstance(ctx.getChild(i), TerminalNode):
                    str += ':'
                else:
                    str += self.visit(ctx.getChild(i))
            return str

    # Visit a parse tree produced by GrammarParser#sliceop.
    def visitSliceop(self, ctx: GrammarParser.SliceopContext):
        if ctx.getChildCount() == 1:
            return ':'
        else:
            return ':' + self.visit(ctx.getChild(1))

    # Visit a parse tree produced by GrammarParser#testlist.
    def visitTestlist(self, ctx: GrammarParser.TestlistContext):
        count = ctx.getChildCount()
        str = ''
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                str += ', '
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#dictorsetmaker.
    def visitDictorsetmaker(self, ctx: GrammarParser.DictorsetmakerContext):
        count = ctx.getChildCount()
        str = ''
        for i in range(count):
            if isinstance(ctx.getChild(i), TerminalNode):
                txt = ctx.getChild(i).getSymbol().text
                if txt == ',':
                    str += ', '
                elif txt == '**':
                    str += '**'
                elif txt == ':':
                    str += ': '
            else:
                str += self.visit(ctx.getChild(i))
        return str

    # Visit a parse tree produced by GrammarParser#classdef.
    def visitClassdef(self, ctx: GrammarParser.ClassdefContext):
        str = 'class ' + ctx.NAME().getSymbol().text
        if ctx.OPEN_PAREN() is not None:
            str += '('
            if ctx.arglist() is not None:
                str += self.visitArglist(ctx.arglist())
            str += ')'
        str += ':'
        self.printer.print(str)
        self.visitSuite(ctx.suite())

    # Visit a parse tree produced by GrammarParser#arglist.
    def visitArglist(self, ctx: GrammarParser.ArglistContext):
        str = ''
        for i in range(len(ctx.num_arg)):
            str += self.visitArgument(ctx.argument(i))
            if i != len(ctx.num_arg) - 1:
                str += ', '
        return str

    # Visit a parse tree produced by GrammarParser#argument.
    def visitArgument(self, ctx: GrammarParser.ArgumentContext):
        str = ''
        if ctx.ASSIGN() is not None:
            str += self.visitTest(ctx.test(0)) + ' = ' + self.visitTest(ctx.test(1))
        elif ctx.POWER() is not None:
            str += ' ** ' + self.visitTest(ctx.test(0))
        elif ctx.STAR() is not None:
            str += ' * ' + self.visitTest(ctx.test(0))
        else:
            str += self.visitTest(ctx.test(0))
            if len(ctx.is_compfor) != 0:
                str += self.visitComp_for(ctx.comp_for())
        return str

    # Visit a parse tree produced by GrammarParser#comp_iter.
    def visitComp_iter(self, ctx: GrammarParser.Comp_iterContext):
        return self.visit(ctx.getChild(0))

    # Visit a parse tree produced by GrammarParser#comp_for.
    def visitComp_for(self, ctx: GrammarParser.Comp_forContext):
        str = ''
        if ctx.ASYNC() is not None:
            str += 'async '
        str += ' for ' + self.visitExprlist(ctx.exprlist()) + ' in ' + self.visitOr_test(ctx.or_test())
        if ctx.comp_iter() is not None:
            if ctx.comp_iter() == []:
                print('visitComp_for problem, this shouldn\'t happen')
            str += self.visitComp_iter(ctx.comp_iter())
        return str

    # Visit a parse tree produced by GrammarParser#comp_if.
    def visitComp_if(self, ctx: GrammarParser.Comp_ifContext):
        str = ' for ' + self.visit(ctx.getChild(0))
        if ctx.comp_iter() is not None:
            str += self.visitComp_iter(ctx.comp_iter())
        return str

    # Visit a parse tree produced by GrammarParser#encoding_decl.
    def visitEncoding_decl(self, ctx: GrammarParser.Encoding_declContext):
        return self.visitChildren(ctx)

    # Visit a parse tree produced by GrammarParser#yield_expr.
    def visitYield_expr(self, ctx: GrammarParser.Yield_exprContext):
        str = 'yield'
        if ctx.getChildCount() == 1:
            return str
        return str + ' ' + self.visit(ctx.getChild(1))

    # Visit a parse tree produced by GrammarParser#yield_arg.
    def visitYield_arg(self, ctx: GrammarParser.Yield_argContext):
        count = ctx.getChildCount()
        if count == 1:
            return self.visit(ctx.getChild(0))
        return 'from ' + self.visit(ctx.getChild(1))
