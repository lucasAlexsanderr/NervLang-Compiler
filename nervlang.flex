import java_cup.runtime.*;

%%

%class Scanner
%unicode
%cup
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* Definitions */
WHITE_SPACE     = [ \t\r\n]+
DIGIT           = [0-9]
INTEGER         = {DIGIT}+
FLOAT           = {DIGIT}+ \. {DIGIT}+
IDENTIFIER      = [a-zA-Z_][a-zA-Z0-9_]*
LINE_COMMENT    = "//" [^\r\n]*
BLOCK_COMMENT   = "/*" [^*] ~"*/" | "/*" "*"+ "/"

%%

/* Whitespace */
{WHITE_SPACE}   { /* ignore */ }

/* Comments */
{LINE_COMMENT}  { /* single-line comment */ }
{BLOCK_COMMENT} { /* multi-line comment */ }

/* Keywords - EVA themed */
"MAGI"          { return symbol(sym.MAGI); }          // program/class
"EVA_UNIT"      { return symbol(sym.EVA_UNIT); }      // class
"PILOT"         { return symbol(sym.PILOT); }         // variable declaration
"ORDER"         { return symbol(sym.ORDER); }         // method definition
"MISSION"       { return symbol(sym.MISSION); }       // for loop
"AT_FIELD"      { return symbol(sym.AT_FIELD); }      // if
"ALTER"         { return symbol(sym.ALTER); }         // else
"LOOP"          { return symbol(sym.LOOP); }          // while
"SYNC"          { return symbol(sym.SYNC); }          // return
"EJECT"         { return symbol(sym.EJECT); }         // break
"PERSIST"       { return symbol(sym.PERSIST); }       // continue
"COMMUNICATE"   { return symbol(sym.COMMUNICATE); }   // print
"DEPLOY"        { return symbol(sym.DEPLOY); }        // import
"VOID"          { return symbol(sym.VOID); }          // void/null

/* Types */
"INTEGER"       { return symbol(sym.INTEGER_TYPE); }  // int
"REAL"          { return symbol(sym.REAL_TYPE); }     // float
"TEXT"          { return symbol(sym.TEXT_TYPE); }     // string
"BOOLEAN"       { return symbol(sym.BOOLEAN_TYPE); }   // bool

/* Boolean constants */
"SYNCHRONIZED"  { return symbol(sym.TRUE); }          // true
"DESYNCHRONIZED" { return symbol(sym.FALSE); }        // false

/* Operators */
"+"             { return symbol(sym.PLUS); }
"-"             { return symbol(sym.MINUS); }
"*"             { return symbol(sym.TIMES); }
"/"             { return symbol(sym.DIVIDE); }
"%"             { return symbol(sym.MODULO); }
"="             { return symbol(sym.ASSIGN); }
"=="            { return symbol(sym.EQUALS); }
"!="            { return symbol(sym.NOT_EQUALS); }
"<"             { return symbol(sym.LESS_THAN); }
">"             { return symbol(sym.GREATER_THAN); }
"<="            { return symbol(sym.LESS_EQUAL); }
">="            { return symbol(sym.GREATER_EQUAL); }
"&&"            { return symbol(sym.AND); }
"||"            { return symbol(sym.OR); }
"!"             { return symbol(sym.NOT); }
"++"            { return symbol(sym.INCREMENT); }
"--"            { return symbol(sym.DECREMENT); }

/* Delimiters */
";"             { return symbol(sym.SEMI); }
","             { return symbol(sym.COMMA); }
"{"             { return symbol(sym.LBRACE); }
"}"             { return symbol(sym.RBRACE); }
"("             { return symbol(sym.LPAREN); }
")"             { return symbol(sym.RPAREN); }
"["             { return symbol(sym.LBRACKET); }
"]"             { return symbol(sym.RBRACKET); }

/* Constants */
{INTEGER}       { return symbol(sym.INT_LITERAL, new Integer(yytext())); }
{FLOAT}         { return symbol(sym.FLOAT_LITERAL, new Float(yytext())); }
\"[^\n\r]*\"   { return symbol(sym.STRING_LITERAL, yytext()); }

/* Identifiers */
{IDENTIFIER}    { return symbol(sym.IDENTIFIER, yytext()); }

/* Error fallback */
.               { System.err.println("Lexical Error at line " + (yyline+1) + ", column " + (yycolumn+1) + ": " + yytext()); }
