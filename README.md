# NervLang Compiler

A compiler frontend for **NervLang**, a Java-like programming language themed around Neon Genesis Evangelion.

## Language Features

NervLang is a Java-inspired language with EVA-themed keywords:

### Keywords
- `MAGI` - class declaration (the supercomputer)
- `EVA_UNIT` - class type
- `PILOT` - variable declaration
- `ORDER` - method definition
- `MISSION` - for loop
- `AT_FIELD` - if statement
- `ALTER` - else statement
- `LOOP` - while loop
- `SYNC` - return statement
- `EJECT` - break statement
- `PERSIST` - continue statement
- `COMMUNICATE` - print statement
- `DEPLOY` - import statement
- `VOID` - void type

### Types
- `INTEGER` - integer
- `REAL` - floating point
- `TEXT` - string
- `BOOLEAN` - boolean

### Constants
- `SYNCHRONIZED` - true
- `DESYNCHRONIZED` - false

### Operators
Arithmetic: `+`, `-`, `*`, `/`, `%`
Comparison: `==`, `!=`, `<`, `>`, `<=`, `>=`
Logical: `&&`, `||`, `!`
Assignment: `=`, `++`, `--`

### Comments
```
// Single-line comment
/* Multi-line
   comment */
```

## Project Structure

```
├── lib/                      # JFlex and JCup JAR files
├── nervlang.flex            # Lexical analyzer specification
├── nervlang.cup             # Parser and semantic analyzer specification
├── Scanner.java             # Generated scanner (from JFlex)
├── parser.java              # Generated parser (from JCup)
├── sym.java                 # Generated symbols (from JCup)
├── Main.java                # Integration driver
├── input.txt                # Sample NervLang program
├── run.sh                   # Build and run script
└── cleanup.sh              # Clean generated files
```

## Building

```bash
./run.sh
```

This will:
1. Generate `Scanner.java` from `nervlang.flex` using JFlex
2. Generate `parser.java` and `sym.java` from `nervlang.cup` using JCup
3. Compile all Java sources
4. Run the compiler on `input.txt`

## Example Program

```nervlang
MAGI EvaUnit01 {
    ORDER INTEGER main() {
        PILOT INTEGER sync_rate = 40;
        
        AT_FIELD (sync_rate > 50) {
            COMMUNICATE("Synchronization successful");
        } ALTER {
            COMMUNICATE("Warning: Low sync rate");
            sync_rate = sync_rate + 10;
        }
        
        LOOP (sync_rate < 100) {
            COMMUNICATE("Increasing sync rate...");
            sync_rate = sync_rate + 5;
        }
        
        SYNC SYNCHRONIZED;
    }
}
```

## Tools Used

- **JFlex 1.9.1** - Lexical analyzer generator
- **Java CUP 11b** - Parser generator
- **Java 26** (OpenJDK)

## Assignment

This project is part of the AV3 assignment for the Aspectos de Compiladores course, implementing a compiler frontend with lexical, syntactic, and semantic analysis.

## License

Academic project for educational purposes.
