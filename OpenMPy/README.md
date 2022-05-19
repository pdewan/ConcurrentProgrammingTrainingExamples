# Installation
1. Make sure you have installed Python3 and Java and that both are on the PATH.
2. Clone the project using:
    ```bash
    git clone https://github.com/calebhuck/OpenMPy.git .
    ```
3. Install the dependencies with:
    ```bash
    pip install -r requirements.txt
    ```
4. Add the bin directory to the PATH environment variable so the Jython launcher can be found:
    ```bash
    export PATH=$PATH:/path/to/project/bin
    ```
    or, for windows
    ```cmd
    export PATH=%PATH%;C:\path\to\project\bin
    ```
5. Set the JYTHON_HOME variable to point to the top level of the project directory.
    ```bash
    export JYTHON_HOME=/path/to/project
    ```
    or, for windows
    ```cmd
    export JYTHON_HOME=C:\path\to\project
    ```
