#!/bin/bash

# Compile the interpreter
echo "Compiling interpreter..."
javac Interpreter.java

if [ $? -ne 0 ]; then
    echo "Compilation failed"
    exit 1
fi

echo "Running NervLang interpreter..."
echo ""

# Run interpreter with input file (default: input.txt)
java Interpreter ${1:-input.txt}

# Cleanup
rm -f *.class
