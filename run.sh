#!/bin/bash
set -e

FLEX="lib/jflex-full-1.9.1.jar"
CUP="lib/java-cup-11b.jar"
LIBS=".:lib/java-cup-11b.jar:lib/java-cup-11b-runtime.jar"
INPUT="${1:-input.txt}"

echo "=== NervLang Compiler & Interpreter ==="
echo ""

# Step 1: Generate scanner from .flex
echo "[1/4] Generating Scanner from nervlang.flex ..."
java -jar "$FLEX" nervlang.flex
echo "      -> Scanner.java generated"

# Step 2: Generate parser from .cup
echo "[2/4] Generating Parser from nervlang.cup ..."
java -jar "$CUP" -parser parser -symbols sym nervlang.cup 2>/dev/null
echo "      -> parser.java and sym.java generated"

# Step 3: Compile all Java sources
echo "[3/4] Compiling Java sources ..."
javac -cp "$LIBS" -Xlint:-deprecation *.java
echo "      -> Compilation successful"

# Step 4: Run analysis (compiler frontend)
echo "[4/4] Running NervLang Compiler on $INPUT ..."
echo ""
echo "--------------------------------------------------------"
java -cp "$LIBS" Main "$INPUT"
echo "--------------------------------------------------------"

# Step 5: Run interpreter (execution)
echo ""
echo "--------------------------------------------------------"
java -cp "$LIBS" Interpreter "$INPUT"
echo "--------------------------------------------------------"
echo ""
echo "=== Done ==="
