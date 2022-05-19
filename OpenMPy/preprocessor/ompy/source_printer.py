class SourcePrinter:

    def __init__(self):
        self.source = ''
        self.indent_level = 0
        self.indent_token = '    '
        #self.newline = '\n'

    def print(self, str):
        for i in range(self.indent_level):
            self.source += self.indent_token
        self.source += str

    def println(self, str):
        for i in range(self.indent_level):
            self.source += self.indent_token
        self.source += str + '\n'

    def newline(self):
        self.source += '\n'

    def indent(self):
        self.indent_level += 1

    def dedent(self):
        if self.indent_level > 0:
            self.indent_level -= 1
        else:
            raise Exception('Error: indent level cannot go below 0')

    def get_source(self):
        return self.source
