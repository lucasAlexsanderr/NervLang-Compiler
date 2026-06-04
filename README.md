# NervLang Compiler & Interpreter

A complete compiler frontend and interpreter for **NervLang**, a Java-like programming language themed around Neon Genesis Evangelion.

## What is NervLang?

NervLang is a fully functional programming language where you can declare classes (`MAGI`), methods (`ORDER`), variables (`PILOT`), and control program flow using EVA-themed keywords. The compiler validates your code (lexical, syntactic, and semantic analysis), and the interpreter actually executes it and shows the output.

## Language Features

### Keywords

- `MAGI` - class declaration (the supercomputer)
- `EVA_UNIT` - class type (reserved)
- `PILOT` - variable declaration
- `ORDER` - method definition
- `MISSION` - for loop
- `AT_FIELD` - if statement
- `ALTER` - else statement
- `LOOP` - while loop
- `SYNC` - return statement
- `EJECT` - break statement
- `PERSIST` - continue statement
- `COMMUNICATE` - print statement (outputs to terminal)
- `DEPLOY` - import statement (reserved)
- `VOID` - void type

### Types

- `INTEGER` - integer numbers
- `REAL` - floating point numbers
- `TEXT` - strings
- `BOOLEAN` - boolean values

### Constants

- `SYNCHRONIZED` - true
- `DESYNCHRONIZED` - false

### Operators

**Arithmetic:** `+`, `-`, `*`, `/`, `%`  
**Comparison:** `==`, `!=`, `<`, `>`, `<=`, `>=`  
**Logical:** `&&`, `||`, `!`  
**Assignment:** `=`, `++`, `--`

### Comments

```nervlang
// Single-line comment
/* Multi-line
   comment */
```

## Example Program

```nervlang
MAGI EvaUnit01 {

    PILOT INTEGER sync_rate = 40;
    PILOT REAL power_level = 100.5;
    PILOT BOOLEAN at_field_active = DESYNCHRONIZED;
    PILOT TEXT pilot_name = "Shinji";

    ORDER INTEGER engage(INTEGER distance) {
        PILOT INTEGER target_distance = 500;

        AT_FIELD (sync_rate > 50) {
            COMMUNICATE("Sync rate OK");
            at_field_active = SYNCHRONIZED;
        } ALTER {
            COMMUNICATE("Warning low sync");
            sync_rate = sync_rate + 10;
        }

        LOOP (distance > target_distance) {
            COMMUNICATE("Advancing to target");
            distance = distance - 50;
        }

        MISSION (PILOT INTEGER phase = 0; phase < 3; phase++) {
            AT_FIELD (phase == 0) {
                COMMUNICATE("Deploying AT Field");
            }
            AT_FIELD (phase == 1) {
                COMMUNICATE("Charging positron rifle");
            }
            AT_FIELD (phase == 2) {
                COMMUNICATE("Firing");
            }
        }

        SYNC sync_rate;
    }

    ORDER VOID emergency_shutdown() {
        COMMUNICATE("Emergency shutdown");
        SYNC;
    }
}
```

## Project Structure

```
├── lib/                      # JFlex and JCup JAR files
│   ├── jflex-full-1.9.1.jar
│   ├── java-cup-11b.jar
│   └── java-cup-11b-runtime.jar
├── nervlang.flex            # Lexical analyzer specification (JFlex)
├── nervlang.cup             # Parser specification (JCup)
├── SemanticAnalyzer.java    # Symbol table and semantic checking
├── Scanner.java             # Generated scanner (from JFlex)
├── parser.java              # Generated parser (from JCup)
├── sym.java                 # Generated symbols (from JCup)
├── Main.java                # Compiler driver (analysis phase)
├── Interpreter.java         # Tree-walking interpreter (execution phase)
├── input.txt                # Sample NervLang program
├── run.sh                   # Full pipeline: compile + run
├── cleanup.sh               # Remove generated files
├── compilation_output.txt   # Sample execution trace
└── README.md                # This file
```

## Quick Start

### Prerequisites

- Java 26 (OpenJDK)
- Unix/Linux shell (bash)

### Running the Compiler + Interpreter

```bash
./run.sh
```

This will:
1. Generate `Scanner.java` from `nervlang.flex` using JFlex
2. Generate `parser.java` and `sym.java` from `nervlang.cup` using JCup
3. Compile all Java sources
4. Run the **compiler** (lexical, syntactic, semantic analysis)
5. Run the **interpreter** (actually execute the code and show output)

### Running with a Custom File

```bash
./run.sh myprogram.nerv
```

### Running Only the Interpreter

```bash
./run-exec.sh
```

### Viewing the Output

The full compilation and execution trace is saved to `compilation_output.txt`:

```bash
cat compilation_output.txt
```

## What You'll See

### Compiler Phase (Analysis)

The compiler validates your NervLang code and prints:

```
[Lexical]   Variable declared: INTEGER sync_rate = ...
[Syntactic] PILOT INTEGER sync_rate = expr; -- OK
[Semantic]  Assigned value to 'sync_rate' of type INTEGER
```

Each line shows:
- **[Lexical]** - Scanner recognized a token
- **[Syntactic]** - Parser validated the grammar
- **[Semantic]** - Analyzer checked types and meaning

### Interpreter Phase (Execution)

The interpreter runs your code and shows actual output:

```
--- Program Output ---

Emergency shutdown

--- Calling patrol(sectors) ---
Patrolling sector
Patrolling sector
Patrolling sector
...

--- Calling engage(distance) ---
Warning low sync
Advancing to target
Advancing to target
...
Deploying AT Field
Charging positron rifle
Firing
Mission failed ejecting
```

## How It Works

### 1. Lexical Analysis

The `.flex` file defines:
- 14 keywords (MAGI, PILOT, ORDER, etc.)
- 4 types (INTEGER, REAL, TEXT, BOOLEAN)
- 17 operators (+, -, *, /, ==, !=, <, >, etc.)
- 8 delimiters (;, {, }, (, ), etc.)
- Literals (integers, floats, strings, identifiers)

JFlex generates a scanner with **164 DFA states** that tokenizes your source code.

### 2. Syntactic Analysis

The `.cup` file defines the NervLang grammar:
- 52 terminals
- 15 non-terminals
- 65 productions (grammar rules)
- 149 parse states

JCup generates a parser that validates the structure of your program.

### 3. Semantic Analysis

`SemanticAnalyzer.java` maintains a symbol table:
- Tracks variable declarations and types
- Checks for undefined variables
- Validates assignments and operations
- Manages scope (class-level vs method-level)

### 4. Interpretation

`Interpreter.java` is a tree-walking interpreter that:
- Parses tokens into an executable representation
- Manages execution environments (class and method scopes)
- Evaluates expressions (arithmetic, comparison, logical)
- Executes control flow (if/else, while, for loops)
- Handles method calls with parameters
- Processes `COMMUNICATE` statements (prints to terminal)
- Manages control flow exceptions (break, continue, return)

## Writing Your Own NervLang Program

Create a file `mycode.nerv`:

```nervlang
MAGI MyClass {

    PILOT INTEGER x = 10;

    ORDER INTEGER main() {
        PILOT INTEGER sum = 0;
        
        MISSION (PILOT INTEGER i = 0; i < 5; i++) {
            sum = sum + i;
            COMMUNICATE("i = ");
            COMMUNICATE(i);
        }
        
        AT_FIELD (sum > 5) {
            COMMUNICATE("Sum is greater than 5");
        }
        
        SYNC sum;
    }
}
```

Run it:

```bash
./run.sh mycode.nerv
```

## Tools Used

- **JFlex 1.9.1** - Lexical analyzer generator
- **Java CUP 11b** - Parser generator
- **Java 26** (OpenJDK)
- **Bash** - Build scripts

## Assignment Requirements

This project implements a complete compiler frontend for the AV3 assignment:

✅ **Lexical Analyzer** (JFlex) - tokenizes keywords, identifiers, operators, literals  
✅ **Syntactic Analyzer** (JCup) - validates grammar structure  
✅ **Semantic Analyzer** - symbol table, type checking, scope management  
✅ **Integration** - Scanner feeds tokens to Parser  
✅ **Sample Program** - `input.txt` demonstrates all features  
✅ **Execution** - Interpreter actually runs the code and shows output  

## License

Academic project for educational purposes.

## Repository

https://github.com/lucasAlexsanderr/NervLang-Compiler

