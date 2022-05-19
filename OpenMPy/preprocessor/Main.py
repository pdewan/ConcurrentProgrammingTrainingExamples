from ompy.antlr_generated.GrammarLexer import GrammarLexer
from ompy.antlr_generated.GrammarVisitor import *
from ompy.antlr_generated.GrammarParser import GrammarParser
from ompy.Translator import Translator
from ompy.source_printer import SourcePrinter

import os
from io import StringIO
import sys


def main():
    if len(sys.argv) != 2:
        raise Exception('Wrong number of command line arguments from jython: this shouln\'t happen')
    #print('file name: ', sys.argv[1])
    filename = sys.argv[1]

    platform = os.name

    j_home = os.getenv('JYTHON_HOME')
    if platform == 'posix':
        if j_home[:-1] != '/':
            j_home = j_home + '/'
    elif platform == 'nt':
        if j_home[:-1] != '\\':
            j_home = j_home + '\\'
    j_home.replace('\\', '\\\\')


    err_output = StringIO()
    sys.stderr = err_output

    input_stream = FileStream(filename)
    lexer = GrammarLexer(input_stream)
    stream = CommonTokenStream(lexer)
    parser = GrammarParser(stream)
    tree = parser.file_input()
    printer = SourcePrinter()
    visitor = Translator(printer)
    visitor.visit(tree)
    if err_output.getvalue() != '':
        print(err_output.getvalue())
    else:
        print('#jython program')
        print('import sys')
        if platform == 'posix':
            print('sys.path.append(\'' + j_home + 'preprocessor/ompy\')')
        elif platform == 'nt':
            print('sys.path.append(\'' + j_home + 'preprocessor\\\\ompy\')')
        else:
            raise Exception('Error: unrecognized platform {}. Should be posix or nt'.format(platform))
        print(printer.get_source())

if __name__ == '__main__':
    main()
