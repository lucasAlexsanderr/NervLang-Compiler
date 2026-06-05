import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Interpreter {

    // Token types
    enum TT {
        MAGI,
        PILOT,
        ORDER,
        AT_FIELD,
        ALTER,
        LOOP,
        MISSION,
        SYNC,
        EJECT,
        PERSIST,
        COMMUNICATE,
        VOID,
        INTEGER_TYPE,
        REAL_TYPE,
        TEXT_TYPE,
        BOOLEAN_TYPE,
        TRUE,
        FALSE,
        PLUS,
        MINUS,
        TIMES,
        DIVIDE,
        MODULO,
        ASSIGN,
        EQUALS,
        NOT_EQUALS,
        LESS_THAN,
        GREATER_THAN,
        LESS_EQUAL,
        GREATER_EQUAL,
        AND,
        OR,
        NOT,
        INCREMENT,
        DECREMENT,
        SEMI,
        COMMA,
        LBRACE,
        RBRACE,
        LPAREN,
        RPAREN,
        INT_LIT,
        FLOAT_LIT,
        STRING_LIT,
        IDENT,
        EOF,
    }

    static class Token {

        TT type;
        String value;
        int line;

        Token(TT t, String v, int l) {
            type = t;
            value = v;
            line = l;
        }

        public String toString() {
            return type + "(" + value + ")";
        }
    }

    // ========== TOKENIZER ==========

    static final String[][] KEYWORD_MAP = {
        { "MAGI", "MAGI" },
        { "PILOT", "PILOT" },
        { "ORDER", "ORDER" },
        { "AT_FIELD", "AT_FIELD" },
        { "ALTER", "ALTER" },
        { "LOOP", "LOOP" },
        { "MISSION", "MISSION" },
        { "SYNC", "SYNC" },
        { "EJECT", "EJECT" },
        { "PERSIST", "PERSIST" },
        { "COMMUNICATE", "COMMUNICATE" },
        { "VOID", "VOID" },
        { "INTEGER", "INTEGER_TYPE" },
        { "REAL", "REAL_TYPE" },
        { "TEXT", "TEXT_TYPE" },
        { "BOOLEAN", "BOOLEAN_TYPE" },
        { "SYNCHRONIZED", "TRUE" },
        { "DESYNCHRONIZED", "FALSE" },
    };

    static List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();
        int i = 0,
            line = 1;
        while (i < source.length()) {
            char c = source.charAt(i);
            if (c == '\n') {
                line++;
                i++;
                continue;
            }
            if (c == ' ' || c == '\t' || c == '\r') {
                i++;
                continue;
            }
            // Comments
            if (
                c == '/' &&
                i + 1 < source.length() &&
                source.charAt(i + 1) == '/'
            ) {
                while (i < source.length() && source.charAt(i) != '\n') i++;
                continue;
            }
            if (
                c == '/' &&
                i + 1 < source.length() &&
                source.charAt(i + 1) == '*'
            ) {
                i += 2;
                while (
                    i + 1 < source.length() &&
                    !(source.charAt(i) == '*' && source.charAt(i + 1) == '/')
                ) {
                    if (source.charAt(i) == '\n') line++;
                    i++;
                }
                i += 2;
                continue;
            }
            // String literal
            if (c == '"') {
                int start = i;
                i++;
                StringBuilder sb = new StringBuilder();
                while (i < source.length() && source.charAt(i) != '"') {
                    sb.append(source.charAt(i));
                    i++;
                }
                i++; // closing "
                tokens.add(new Token(TT.STRING_LIT, sb.toString(), line));
                continue;
            }
            // Number
            if (Character.isDigit(c)) {
                int start = i;
                while (
                        i < source.length() &&
                        (Character.isDigit(source.charAt(i)) ||
                            source.charAt(i) == '.')
                    )
                    i++;
                String num = source.substring(start, i);
                if (num.contains(".")) tokens.add(
                    new Token(TT.FLOAT_LIT, num, line)
                );
                else tokens.add(new Token(TT.INT_LIT, num, line));
                continue;
            }
            // Identifier / keyword
            if (Character.isLetter(c) || c == '_') {
                int start = i;
                while (
                        i < source.length() &&
                        (Character.isLetterOrDigit(source.charAt(i)) ||
                            source.charAt(i) == '_')
                    )
                    i++;
                String word = source.substring(start, i);
                boolean found = false;
                for (String[] kw : KEYWORD_MAP) {
                    if (kw[0].equals(word)) {
                        tokens.add(new Token(TT.valueOf(kw[1]), word, line));
                        found = true;
                        break;
                    }
                }
                if (!found) tokens.add(new Token(TT.IDENT, word, line));
                continue;
            }
            // Two-char operators
            if (i + 1 < source.length()) {
                String two = "" + c + source.charAt(i + 1);
                switch (two) {
                    case "==":
                        tokens.add(new Token(TT.EQUALS, "==", line));
                        i += 2;
                        continue;
                    case "!=":
                        tokens.add(new Token(TT.NOT_EQUALS, "!=", line));
                        i += 2;
                        continue;
                    case "<=":
                        tokens.add(new Token(TT.LESS_EQUAL, "<=", line));
                        i += 2;
                        continue;
                    case ">=":
                        tokens.add(new Token(TT.GREATER_EQUAL, ">=", line));
                        i += 2;
                        continue;
                    case "&&":
                        tokens.add(new Token(TT.AND, "&&", line));
                        i += 2;
                        continue;
                    case "||":
                        tokens.add(new Token(TT.OR, "||", line));
                        i += 2;
                        continue;
                    case "++":
                        tokens.add(new Token(TT.INCREMENT, "++", line));
                        i += 2;
                        continue;
                    case "--":
                        tokens.add(new Token(TT.DECREMENT, "--", line));
                        i += 2;
                        continue;
                }
            }
            // Single-char operators/delimiters
            switch (c) {
                case '+':
                    tokens.add(new Token(TT.PLUS, "+", line));
                    break;
                case '-':
                    tokens.add(new Token(TT.MINUS, "-", line));
                    break;
                case '*':
                    tokens.add(new Token(TT.TIMES, "*", line));
                    break;
                case '/':
                    tokens.add(new Token(TT.DIVIDE, "/", line));
                    break;
                case '%':
                    tokens.add(new Token(TT.MODULO, "%", line));
                    break;
                case '=':
                    tokens.add(new Token(TT.ASSIGN, "=", line));
                    break;
                case '<':
                    tokens.add(new Token(TT.LESS_THAN, "<", line));
                    break;
                case '>':
                    tokens.add(new Token(TT.GREATER_THAN, ">", line));
                    break;
                case '!':
                    tokens.add(new Token(TT.NOT, "!", line));
                    break;
                case ';':
                    tokens.add(new Token(TT.SEMI, ";", line));
                    break;
                case ',':
                    tokens.add(new Token(TT.COMMA, ",", line));
                    break;
                case '{':
                    tokens.add(new Token(TT.LBRACE, "{", line));
                    break;
                case '}':
                    tokens.add(new Token(TT.RBRACE, "}", line));
                    break;
                case '(':
                    tokens.add(new Token(TT.LPAREN, "(", line));
                    break;
                case ')':
                    tokens.add(new Token(TT.RPAREN, ")", line));
                    break;
            }
            i++;
        }
        tokens.add(new Token(TT.EOF, "", line));
        return tokens;
    }

    // ========== PARSER STATE ==========

    List<Token> tokens;
    int pos;
    // Runtime environments: class-level and method-level
    Map<String, Object> classEnv = new HashMap<>();
    Map<String, Object> localEnv;
    Map<String, MethodDef> methods = new HashMap<>();
    String className;

    Token peek() {
        return tokens.get(pos);
    }

    Token advance() {
        return tokens.get(pos++);
    }

    void expect(TT t) {
        if (peek().type != t) error("Expected " + t + " but got " + peek());
        advance();
    }

    void error(String msg) {
        throw new RuntimeException(
            "[Runtime Error] Line " + peek().line + ": " + msg
        );
    }

    // ========== AST NODES ==========

    static class MethodDef {

        String name, retType;
        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();
        List<Object> body; // statements are kept as token lists
        int bodyStart, bodyEnd; // token indices
    }

    // ========== EXECUTION FLOW CONTROLS ==========
    static class ReturnException extends RuntimeException {

        Object val;

        ReturnException(Object v) {
            val = v;
        }
    }

    static class BreakException extends RuntimeException {}

    static class ContinueException extends RuntimeException {}

    // ========== MAIN PARSE + EXEC ==========

    void run(String source) {
        tokens = tokenize(source);
        pos = 0;

        System.out.println();
        System.out.println(
            "========================================================"
        );
        System.out.println("  NervLang Interpreter - Execution Phase");
        System.out.println(
            "========================================================"
        );
        System.out.println();

        // Parse: MAGI ClassName { members }
        expect(TT.MAGI);
        className = advance().value;
        System.out.println(">>> Class '" + className + "' loaded");
        expect(TT.LBRACE);

        // Parse members: variable declarations and method declarations
        while (peek().type != TT.RBRACE) {
            if (peek().type == TT.PILOT) {
                parseClassVar();
            } else if (peek().type == TT.ORDER) {
                parseMethod();
            } else {
                error("Unexpected token in class body: " + peek());
            }
        }
        expect(TT.RBRACE);

        System.out.println(">>> " + methods.size() + " method(s) found");
        System.out.println();
        System.out.println("--- Program Output ---");
        System.out.println();

        // Call the main method if it exists, otherwise call the first method
        if (methods.containsKey("main")) {
            callMethod("main", new Object[0]);
        } else {
            // Execute the first method found
            String first = null;
            for (String k : methods.keySet()) {
                // Skip methods with required params; call the one we can
            }
            // Just call all no-param methods for demo
            for (String k : methods.keySet()) {
                MethodDef m = methods.get(k);
                if (m.paramNames.isEmpty()) {
                    callMethod(k, new Object[0]);
                    System.out.println();
                }
            }
            // Also demonstrate calling methods with params using smart defaults
            for (String k : methods.keySet()) {
                MethodDef m = methods.get(k);
                if (!m.paramNames.isEmpty()) {
                    System.out.println(
                        "--- Calling " +
                            k +
                            "(" +
                            String.join(", ", m.paramNames) +
                            ") ---"
                    );
                    Object[] args = new Object[m.paramNames.size()];
                    for (int i = 0; i < args.length; i++) {
                        String t = m.paramTypes.get(i);
                        // Use context-aware defaults based on method name hints
                        if (t.equals("INTEGER")) {
                            if (
                                k.contains("engage") ||
                                m.paramNames.get(i).contains("distance")
                            ) args[i] = 1500;
                            else args[i] = 10;
                        } else if (t.equals("REAL")) args[i] = 50.0;
                        else if (t.equals("TEXT")) args[i] = "NERV";
                        else if (t.equals("BOOLEAN")) args[i] = true;
                    }
                    callMethod(k, args);
                    System.out.println();
                }
            }
        }

        System.out.println("--- Execution Complete ---");
        System.out.println();
        System.out.println(
            "========================================================"
        );
        System.out.println("  NervLang program finished successfully");
        System.out.println(
            "========================================================"
        );
    }

    void parseClassVar() {
        expect(TT.PILOT);
        String type = advance().value;
        String name = advance().value;
        Object val = null;
        if (peek().type == TT.ASSIGN) {
            advance();
            val = parseConstExpr();
            expect(TT.SEMI);
        } else {
            expect(TT.SEMI);
            val = defaultValue(type);
        }
        classEnv.put(name, val);
    }

    Object defaultValue(String type) {
        if (type.equals("INTEGER")) return 0;
        if (type.equals("REAL")) return 0.0;
        if (type.equals("TEXT")) return "";
        if (type.equals("BOOLEAN")) return false;
        return null;
    }

    void parseMethod() {
        expect(TT.ORDER);
        String retType = advance().value;
        String name = advance().value;
        expect(TT.LPAREN);

        MethodDef md = new MethodDef();
        md.name = name;
        md.retType = retType;

        // Parse params
        if (peek().type != TT.RPAREN) {
            do {
                String pt = advance().value;
                String pn = advance().value;
                md.paramTypes.add(pt);
                md.paramNames.add(pn);
                if (peek().type == TT.COMMA) advance();
                else break;
            } while (true);
        }
        expect(TT.RPAREN);

        // Store body token range (between { and })
        expect(TT.LBRACE);
        int start = pos;
        int depth = 1;
        while (depth > 0) {
            if (peek().type == TT.LBRACE) depth++;
            if (peek().type == TT.RBRACE) depth--;
            if (depth > 0) advance();
        }
        md.bodyEnd = pos;
        md.bodyStart = start;
        expect(TT.RBRACE);
        methods.put(name, md);
    }

    // ========== EXPRESSION PARSING (for constant init values only) ==========

    Object parseConstExpr() {
        if (peek().type == TT.INT_LIT) {
            return Integer.parseInt(advance().value);
        }
        if (peek().type == TT.FLOAT_LIT) {
            return Double.parseDouble(advance().value);
        }
        if (peek().type == TT.STRING_LIT) {
            return advance().value;
        }
        if (peek().type == TT.TRUE) {
            advance();
            return true;
        }
        if (peek().type == TT.FALSE) {
            advance();
            return false;
        }
        if (peek().type == TT.IDENT) {
            // Could be a reference to a previously declared variable
            String n = advance().value;
            if (classEnv.containsKey(n)) return classEnv.get(n);
            return n; // just return the name
        }
        error("Cannot parse constant expression: " + peek());
        return null;
    }

    // ========== METHOD EXECUTION ==========

    Object callMethod(String name, Object[] args) {
        MethodDef m = methods.get(name);
        if (m == null) {
            error("Undefined method: " + name);
            return null;
        }

        // Save and set up local env
        Map<String, Object> savedLocal = localEnv;
        localEnv = new HashMap<>(classEnv); // copy class vars
        for (int i = 0; i < args.length; i++) {
            localEnv.put(m.paramNames.get(i), args[i]);
        }

        // Execute body tokens
        int savedPos = pos;
        pos = m.bodyStart;

        Object result = null;
        try {
            while (pos < m.bodyEnd) {
                executeStatement();
            }
        } catch (ReturnException r) {
            result = r.val;
        } catch (BreakException e) {
            // EJECT outside loop → treat as early method exit
            System.out.println("[EJECT] Early method exit from " + name);
        }

        pos = savedPos;
        localEnv = savedLocal;
        return result;
    }

    // ========== STATEMENT EXECUTION ==========

    void executeStatement() {
        TT t = peek().type;

        if (t == TT.PILOT) {
            // variable declaration
            advance();
            String type = advance().value;
            String name = advance().value;
            if (peek().type == TT.ASSIGN) {
                advance();
                Object val = evalExpr();
                localEnv.put(name, val);
                expect(TT.SEMI);
            } else {
                localEnv.put(name, defaultValue(type));
                expect(TT.SEMI);
            }
        } else if (t == TT.COMMUNICATE) {
            // print
            advance();
            expect(TT.LPAREN);
            Object val = evalExpr();
            expect(TT.RPAREN);
            expect(TT.SEMI);
            System.out.println(val);
        } else if (t == TT.AT_FIELD) {
            // if / if-else
            advance();
            expect(TT.LPAREN);
            Object cond = evalExpr();
            expect(TT.RPAREN);
            expect(TT.LBRACE);

            if (toBool(cond)) {
                execBlock(); // Executa o bloco IF
                // CORREÇÃO: Ignorar o ALTER se o IF foi executado
                if (peek().type == TT.ALTER) {
                    advance();
                    expect(TT.LBRACE);
                    skipBlock();
                }
            } else {
                skipBlock(); // Pula o bloco IF
                // Executa o ALTER se o IF foi falso
                if (peek().type == TT.ALTER) {
                    advance();
                    expect(TT.LBRACE);
                    execBlock();
                }
            }
        } else if (t == TT.LOOP) {
            // while
            advance();
            expect(TT.LPAREN);
            int condStart = pos;
            // Read condition tokens
            skipToMatchingParen();
            int condEnd = pos - 1; // before )
            expect(TT.LBRACE);
            int bodyStart = pos;
            skipBlock();
            int bodyEnd = pos - 1; // before }

            // Execute loop
            int maxIter = 10000;
            while (maxIter-- > 0) {
                pos = condStart;
                Object cond = evalExpr();
                pos = bodyEnd + 1; // after }
                if (!toBool(cond)) break;
                try {
                    pos = bodyStart;
                    while (pos < bodyEnd) {
                        executeStatement();
                    }
                } catch (BreakException e) {
                    break;
                } catch (ContinueException e) {
                    /* continue loop */
                }
            }
        } else if (t == TT.MISSION) {
            // for
            advance();
            expect(TT.LPAREN);

            // Init
            if (peek().type == TT.PILOT) {
                advance();
                String type = advance().value;
                String name = advance().value;
                expect(TT.ASSIGN);
                Object val = evalExpr();
                localEnv.put(name, val);
            } else {
                String name = advance().value;
                expect(TT.ASSIGN);
                Object val = evalExpr();
                localEnv.put(name, val);
            }
            expect(TT.SEMI);

            // Condition (save position range)
            int condStart = pos;
            int depth = 0;
            while (!(depth == 0 && peek().type == TT.SEMI)) {
                if (peek().type == TT.LPAREN) depth++;
                if (peek().type == TT.RPAREN) depth--;
                advance();
            }
            int condEnd = pos; // at SEMI
            expect(TT.SEMI);

            // Step
            int stepStart = pos;
            depth = 0;
            while (!(depth == 0 && peek().type == TT.RPAREN)) {
                if (peek().type == TT.LPAREN) depth++;
                if (peek().type == TT.RPAREN) depth--;
                advance();
            }
            int stepEnd = pos;
            expect(TT.RPAREN);

            // Body
            expect(TT.LBRACE);
            int bodyStart = pos;
            skipBlock();
            int bodyEnd = pos - 1;

            // Execute loop
            int maxIter = 10000;
            while (maxIter-- > 0) {
                pos = condStart;
                Object cond = evalExpr();
                pos = bodyEnd + 1;
                if (!toBool(cond)) break;
                try {
                    pos = bodyStart;
                    while (pos < bodyEnd) {
                        executeStatement();
                    }
                } catch (BreakException e) {
                    break;
                } catch (ContinueException e) {
                    /* skip to step */
                }

                // Step
                int savedPos = pos;
                pos = stepStart;
                executeStep();
                pos = savedPos;
            }
        } else if (t == TT.SYNC) {
            // return
            advance();
            if (peek().type == TT.SEMI) {
                advance();
                throw new ReturnException(null);
            } else {
                Object val = evalExpr();
                expect(TT.SEMI);
                throw new ReturnException(val);
            }
        } else if (t == TT.EJECT) {
            // break
            advance();
            expect(TT.SEMI);
            throw new BreakException();
        } else if (t == TT.PERSIST) {
            // continue
            advance();
            expect(TT.SEMI);
            throw new ContinueException();
        } else if (t == TT.IDENT) {
            String name = advance().value;
            if (peek().type == TT.ASSIGN) {
                advance();
                Object val = evalExpr();
                localEnv.put(name, val);
                expect(TT.SEMI);
            } else if (peek().type == TT.INCREMENT) {
                advance();
                Object cur = localEnv.getOrDefault(name, 0);
                if (cur instanceof Double) localEnv.put(
                    name,
                    (Double) cur + 1.0
                );
                else localEnv.put(name, toInt(cur) + 1);
                expect(TT.SEMI);
            } else if (peek().type == TT.DECREMENT) {
                advance();
                Object cur = localEnv.getOrDefault(name, 0);
                if (cur instanceof Double) localEnv.put(
                    name,
                    (Double) cur - 1.0
                );
                else localEnv.put(name, toInt(cur) - 1);
                expect(TT.SEMI);
            } else if (peek().type == TT.LPAREN) {
                // method call
                advance();
                List<Object> args = new ArrayList<>();
                if (peek().type != TT.RPAREN) {
                    args.add(evalExpr());
                    while (peek().type == TT.COMMA) {
                        advance();
                        args.add(evalExpr());
                    }
                }
                expect(TT.RPAREN);
                expect(TT.SEMI);
                callMethod(name, args.toArray());
            } else {
                error("Unexpected after identifier: " + peek());
            }
        } else {
            error("Unknown statement: " + peek());
        }
    }

    void executeStep() {
        String name = advance().value;
        if (peek().type == TT.INCREMENT) {
            advance();
            Object cur = localEnv.getOrDefault(name, 0);
            if (cur instanceof Double) localEnv.put(name, (Double) cur + 1.0);
            else localEnv.put(name, toInt(cur) + 1);
        } else if (peek().type == TT.DECREMENT) {
            advance();
            Object cur = localEnv.getOrDefault(name, 0);
            if (cur instanceof Double) localEnv.put(name, (Double) cur - 1.0);
            else localEnv.put(name, toInt(cur) - 1);
        } else if (peek().type == TT.ASSIGN) {
            advance();
            Object val = evalExpr();
            localEnv.put(name, val);
        }
    }

    void execBlock() {
        // Execute until matching }
        while (peek().type != TT.RBRACE) {
            executeStatement();
        }
        expect(TT.RBRACE);
    }

    void skipBlock() {
        int depth = 1;
        while (depth > 0) {
            if (peek().type == TT.LBRACE) depth++;
            if (peek().type == TT.RBRACE) depth--;
            if (depth > 0) advance();
        }
        expect(TT.RBRACE);
    }

    void skipToMatchingParen() {
        int depth = 1;
        while (true) {
            if (peek().type == TT.LPAREN) depth++;
            if (peek().type == TT.RPAREN) {
                depth--;
                if (depth == 0) break;
            }
            advance();
        }
        expect(TT.RPAREN);
    }

    // ========== EXPRESSION EVALUATOR ==========

    Object evalExpr() {
        return evalOr();
    }

    Object evalOr() {
        Object left = evalAnd();
        while (peek().type == TT.OR) {
            advance();
            Object right = evalAnd();
            left = toBool(left) || toBool(right);
        }
        return left;
    }

    Object evalAnd() {
        Object left = evalEquality();
        while (peek().type == TT.AND) {
            advance();
            Object right = evalEquality();
            left = toBool(left) && toBool(right);
        }
        return left;
    }

    Object evalEquality() {
        Object left = evalComparison();
        if (peek().type == TT.EQUALS) {
            advance();
            Object right = evalComparison();
            if (left instanceof Number && right instanceof Number) return (
                ((Number) left).doubleValue() == ((Number) right).doubleValue()
            );
            if (
                left instanceof Boolean && right instanceof Boolean
            ) return left.equals(right);
            return Objects.equals(left, right);
        }
        if (peek().type == TT.NOT_EQUALS) {
            advance();
            Object right = evalComparison();
            if (left instanceof Number && right instanceof Number) return (
                ((Number) left).doubleValue() != ((Number) right).doubleValue()
            );
            if (
                left instanceof Boolean && right instanceof Boolean
            ) return !left.equals(right);
            return !Objects.equals(left, right);
        }
        return left;
    }

    Object evalComparison() {
        Object left = evalAddSub();
        if (peek().type == TT.LESS_THAN) {
            advance();
            return toDouble(left) < toDouble(evalAddSub());
        }
        if (peek().type == TT.GREATER_THAN) {
            advance();
            return toDouble(left) > toDouble(evalAddSub());
        }
        if (peek().type == TT.LESS_EQUAL) {
            advance();
            return toDouble(left) <= toDouble(evalAddSub());
        }
        if (peek().type == TT.GREATER_EQUAL) {
            advance();
            return toDouble(left) >= toDouble(evalAddSub());
        }
        return left;
    }

    Object evalAddSub() {
        Object left = evalMulDiv();
        while (peek().type == TT.PLUS || peek().type == TT.MINUS) {
            TT op = advance().type;
            Object right = evalMulDiv();
            if (left instanceof String && op == TT.PLUS) {
                left = left + toString(right);
            } else {
                if (left instanceof Double || right instanceof Double) left =
                    toDouble(left) + (op == TT.PLUS ? 1 : -1) * toDouble(right);
                else if (op == TT.PLUS) left = toInt(left) + toInt(right);
                else left = toInt(left) - toInt(right);
            }
        }
        return left;
    }

    Object evalMulDiv() {
        Object left = evalUnary();
        while (
            peek().type == TT.TIMES ||
            peek().type == TT.DIVIDE ||
            peek().type == TT.MODULO
        ) {
            TT op = advance().type;
            Object right = evalUnary();
            if (left instanceof Double || right instanceof Double) {
                double r = toDouble(right);
                if (op == TT.TIMES) left = toDouble(left) * r;
                else if (op == TT.DIVIDE) left = toDouble(left) / r;
                else left = toDouble(left) % r;
            } else {
                int r = toInt(right);
                if (op == TT.TIMES) left = toInt(left) * r;
                else if (op == TT.DIVIDE) left = toInt(left) / r;
                else left = toInt(left) % r;
            }
        }
        return left;
    }

    Object evalUnary() {
        if (peek().type == TT.MINUS) {
            advance();
            Object val = evalPrimary();
            if (val instanceof Double) return -((Double) val);
            return -toInt(val);
        }
        if (peek().type == TT.NOT) {
            advance();
            return !toBool(evalPrimary());
        }
        return evalPrimary();
    }

    Object evalPrimary() {
        TT t = peek().type;
        if (t == TT.INT_LIT) return Integer.parseInt(advance().value);
        if (t == TT.FLOAT_LIT) return Double.parseDouble(advance().value);
        if (t == TT.STRING_LIT) return advance().value;
        if (t == TT.TRUE) {
            advance();
            return true;
        }
        if (t == TT.FALSE) {
            advance();
            return false;
        }
        if (t == TT.LPAREN) {
            advance();
            Object val = evalExpr();
            expect(TT.RPAREN);
            return val;
        }
        if (t == TT.IDENT) {
            String name = advance().value;
            if (peek().type == TT.LPAREN) {
                advance();
                List<Object> args = new ArrayList<>();
                if (peek().type != TT.RPAREN) {
                    args.add(evalExpr());
                    while (peek().type == TT.COMMA) {
                        advance();
                        args.add(evalExpr());
                    }
                }
                expect(TT.RPAREN);
                Object result = callMethod(name, args.toArray());
                return result != null ? result : 0;
            }
            // Variable lookup
            if (
                localEnv != null && localEnv.containsKey(name)
            ) return localEnv.get(name);
            if (classEnv.containsKey(name)) return classEnv.get(name);
            error("Undefined variable: " + name);
        }
        error("Unexpected in expression: " + peek());
        return null;
    }

    // ========== CONVERSION HELPERS ==========

    double toDouble(Object o) {
        if (o instanceof Double) return (Double) o;
        if (o instanceof Integer) return (Integer) o;
        return Double.parseDouble(o.toString());
    }

    int toInt(Object o) {
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Double) return (int) ((double) (Double) o);
        return Integer.parseInt(o.toString());
    }

    Number toNum(Object o) {
        if (o instanceof Number) return (Number) o;
        return 0;
    }

    boolean toBool(Object o) {
        if (o instanceof Boolean) return (Boolean) o;
        if (o instanceof Number) return ((Number) o).doubleValue() != 0;
        return o != null;
    }

    String toString(Object o) {
        if (o == null) return "null";
        return o.toString();
    }

    // ========== ENTRY POINT ==========

    public static void main(String[] args) {
        String file = (args.length > 0) ? args[0] : "input.txt";
        try {
            String source = new String(
                java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file))
            );
            new Interpreter().run(source);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
