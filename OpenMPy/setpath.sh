#!/bin/bash

JYTHON_HOME=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

export PATH="$PATH:$JYTHON_HOME/bin" 
export JYTHON_HOME=$JYTHON_HOME
