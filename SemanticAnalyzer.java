import java.util.*;

public class SemanticAnalyzer {
    
    public static class SymbolEntry {
        public String name;
        public String type;
        public String scope;
        public boolean isMethod;
        public String returnType;
        public int line;
        public int col;

        public SymbolEntry(String name, String type, String scope, int line, int col) {
            this.name = name;
            this.type = type;
            this.scope = scope;
            this.isMethod = false;
            this.returnType = null;
            this.line = line;
            this.col = col;
        }
    }

    private Map<String, SymbolEntry> symbolTable = new HashMap<>();
    private String currentClass = "";
    private String currentMethod = "";

    public void setCurrentClass(String c) { currentClass = c; }
    public String getCurrentClass() { return currentClass; }
    public void setCurrentMethod(String m) { currentMethod = m; }
    public String getCurrentMethod() { return currentMethod; }

    public void declareVar(String name, String type, String scope, int line, int col) {
        String key = scope + "." + name;
        if (symbolTable.containsKey(key)) {
            System.out.println("[Semantic Error] Line " + (line+1) 
                + ": Duplicate declaration '" + name + "'");
        } else {
            symbolTable.put(key, new SymbolEntry(name, type, scope, line, col));
        }
    }

    public void declareMethod(String name, String returnType, String scope, int line, int col) {
        String key = scope + "." + name;
        if (symbolTable.containsKey(key)) {
            System.out.println("[Semantic Error] Line " + (line+1) 
                + ": Duplicate method '" + name + "'");
        } else {
            SymbolEntry entry = new SymbolEntry(name, returnType, scope, line, col);
            entry.isMethod = true;
            entry.returnType = returnType;
            symbolTable.put(key, entry);
        }
    }

    public void declareClass(String name, int line, int col) {
        String key = "global." + name;
        SymbolEntry entry = new SymbolEntry(name, "CLASS", "global", line, col);
        symbolTable.put(key, entry);
    }

    public SymbolEntry lookup(String name, String scope) {
        SymbolEntry e = symbolTable.get(scope + "." + name);
        if (e == null && !scope.equals("global"))
            e = symbolTable.get("global." + name);
        if (e == null)
            e = symbolTable.get("." + name);
        return e;
    }

    public void printSymbolTable() {
        System.out.println("Symbol Table (" + symbolTable.size() + " entries):");
        for (Map.Entry<String, SymbolEntry> e : symbolTable.entrySet()) {
            SymbolEntry se = e.getValue();
            if (se.isMethod) {
                System.out.println("  METHOD " + se.returnType + " " + se.name + "() [" + se.scope + "]");
            } else {
                System.out.println("  VAR " + se.type + " " + se.name + " [" + se.scope + "]");
            }
        }
    }
}
