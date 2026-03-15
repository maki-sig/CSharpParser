// finished scanner class for C# as of March 15
// compiled by: Crisler Annilov B. Padrinao

import java.io.*;
import java.util.*;

public class projectreq3_BOTIS_PADRINAO {

    enum TokenType {
        KEYWORD, IDENTIFIER, INTEGER_LITERAL, FLOAT_LITERAL,
        STRING_LITERAL, CHAR_LITERAL, BOOL_LITERAL, NULL_LITERAL,
        OPERATOR, DELIMITER, ILLEGAL
    }

    static class Token {
        TokenType type;
        String value;
        int line;

        Token(TokenType type, String value, int line) {
            this.type = type;
            this.value = value;
            this.line = line;
        }

        @Override
        public String toString() {
            return String.format("%-5d | %-20s | %s", line, type, value);
        }
    }

    // C# reserved words
    static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char",
            "checked", "class", "const", "continue", "decimal", "default", "delegate",
            "do", "double", "else", "enum", "event", "explicit", "extern", "false",
            "finally", "fixed", "float", "for", "foreach", "goto", "if", "implicit",
            "in", "int", "interface", "internal", "is", "lock", "long", "namespace",
            "new", "null", "object", "operator", "out", "override", "params", "private",
            "protected", "public", "readonly", "ref", "return", "sbyte", "sealed",
            "short", "sizeof", "stackalloc", "static", "string", "struct", "switch",
            "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked",
            "unsafe", "ushort", "using", "virtual", "void", "volatile", "while", "var",
            "async", "await", "yield", "get", "set", "value", "add", "remove", "partial",
            "dynamic", "record", "init", "with", "nint", "nuint", "and", "or", "not"));

    // Multi-char operators are checked before single-char ones
    static final String[] MULTI_OPS = {
            "<<=", ">>=", "??=", "?.", "?[",
            "++", "--", "&&", "||", "??", "==", "!=", "<=", ">=",
            "<<", ">>", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
            "=>", "->"
    };

    static final Set<Character> SINGLE_OPS = new HashSet<>(Arrays.asList(
            '+', '-', '*', '/', '%', '=', '<', '>', '!', '&', '|', '^', '~', '?'));

    static final Set<Character> DELIMITERS = new HashSet<>(Arrays.asList(
            '(', ')', '{', '}', '[', ']', ';', ',', '.', ':'));

    private final String src;
    private int pos = 0;
    private int line = 1;

    projectreq3_BOTIS_PADRINAO(String src) {
        this.src = src;
    }

    private boolean atEnd() {
        return pos >= src.length();
    }

    private char current() {
        return src.charAt(pos);
    }

    private char peek(int offset) {
        int i = pos + offset;
        return i < src.length() ? src.charAt(i) : '\0';
    }

    private char advance() {
        return src.charAt(pos++);
    }

    // Main tokenizer loop
    List<Token> tokenise() {
        List<Token> tokens = new ArrayList<>();

        while (!atEnd()) {
            skipWhitespaceAndComments();
            if (atEnd())
                break;

            int tokenLine = line;
            char c = current();

            if (c == '"' || (c == '@' && peek(1) == '"')) {
                tokens.add(readString(tokenLine));
            } else if (c == '\'') {
                tokens.add(readChar(tokenLine));
            } else if (Character.isDigit(c) || (c == '.' && Character.isDigit(peek(1)))) {
                tokens.add(readNumber(tokenLine));
            } else if (Character.isLetter(c) || c == '_') {
                tokens.add(readWord(tokenLine));
            } else if (isStartOfMultiOp()) {
                tokens.add(readMultiOp(tokenLine));
            } else if (SINGLE_OPS.contains(c)) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(advance()), tokenLine));
            } else if (DELIMITERS.contains(c)) {
                tokens.add(new Token(TokenType.DELIMITER, String.valueOf(advance()), tokenLine));
            } else {
                // Unknown character — report as illegal, skip blanks
                char bad = advance();
                if (!String.valueOf(bad).isBlank()) {
                    tokens.add(new Token(TokenType.ILLEGAL,
                            "Illegal character '" + bad + "' (ASCII " + (int) bad + ")", tokenLine));
                }
            }
        }

        return tokens;
    }

    // Skip spaces, newlines, and both comment types
    private void skipWhitespaceAndComments() {
        while (!atEnd()) {
            char c = current();

            if (c == '\n') {
                line++;
                pos++;
            } else if (Character.isWhitespace(c)) {
                pos++;
            } else if (c == '/' && peek(1) == '/') {
                while (!atEnd() && current() != '\n') {
                    pos++;
                }
            } else if (c == '/' && peek(1) == '*') {
                pos += 2;
                while (!atEnd()) {
                    if (current() == '\n') {
                        line++;
                    }
                    if (current() == '*' && peek(1) == '/') {
                        pos += 2;
                        break;
                    }
                    pos++;
                }
            } else {
                break;
            }
        }
    }

    // Handles regular "..." and verbatim @"..." strings
    private Token readString(int tokenLine) {
        StringBuilder sb = new StringBuilder();
        boolean verbatim = (current() == '@');
        if (verbatim) {
            sb.append(advance());
        }
        sb.append(advance()); // opening quote

        while (!atEnd()) {
            char c = current();
            if (c == '\n') {
                line++;
            }
            if (verbatim) {
                if (c == '"') {
                    sb.append(advance());
                    if (!atEnd() && current() == '"') {
                        sb.append(advance()); // "" escape in verbatim
                    } else {
                        break;
                    }
                } else {
                    sb.append(advance());
                }
            } else {
                if (c == '\\') {
                    sb.append(advance());
                    if (!atEnd()) {
                        sb.append(advance());
                    }
                } else if (c == '"') {
                    sb.append(advance());
                    break;
                } else {
                    sb.append(advance());
                }
            }
        }
        return new Token(TokenType.STRING_LITERAL, sb.toString(), tokenLine);
    }

    // Reads a char literal like 'a' or '\n'
    private Token readChar(int tokenLine) {
        StringBuilder sb = new StringBuilder();
        sb.append(advance()); // opening '
        while (!atEnd() && current() != '\'') {
            if (current() == '\\') {
                sb.append(advance());
            }
            if (!atEnd()) {
                sb.append(advance());
            }
        }
        if (!atEnd()) {
            sb.append(advance()); // closing '
        }
        return new Token(TokenType.CHAR_LITERAL, sb.toString(), tokenLine);
    }

    // Handles int, float, hex (0x), binary (0b), and suffixes (f, d, m, L, u)
    private Token readNumber(int tokenLine) {
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;

        if (current() == '0' && (peek(1) == 'x' || peek(1) == 'X')) {
            sb.append(advance());
            sb.append(advance());
            while (!atEnd() && isHexDigit(current())) {
                sb.append(advance());
            }
        } else if (current() == '0' && (peek(1) == 'b' || peek(1) == 'B')) {
            sb.append(advance());
            sb.append(advance());
            while (!atEnd() && (current() == '0' || current() == '1')) {
                sb.append(advance());
            }
        } else {
            while (!atEnd() && (Character.isDigit(current()) || current() == '_')) {
                sb.append(advance());
            }
            if (!atEnd() && current() == '.' && Character.isDigit(peek(1))) {
                isFloat = true;
                sb.append(advance());
                while (!atEnd() && (Character.isDigit(current()) || current() == '_')) {
                    sb.append(advance());
                }
            }
            if (!atEnd() && (current() == 'e' || current() == 'E')) {
                isFloat = true;
                sb.append(advance());
                if (!atEnd() && (current() == '+' || current() == '-')) {
                    sb.append(advance());
                }
                while (!atEnd() && Character.isDigit(current())) {
                    sb.append(advance());
                }
            }
            if (!atEnd() && "fFdDmMlLuU".indexOf(current()) >= 0) {
                if ("fFdD".indexOf(current()) >= 0) {
                    isFloat = true;
                }
                sb.append(advance());
                if (!atEnd() && "lLuU".indexOf(current()) >= 0) {
                    sb.append(advance());
                }
            }
        }

        return new Token(isFloat ? TokenType.FLOAT_LITERAL : TokenType.INTEGER_LITERAL,
                sb.toString(), tokenLine);
    }

    // Reads a word then decides if it's a keyword, bool, null, or identifier
    private Token readWord(int tokenLine) {
        StringBuilder sb = new StringBuilder();
        while (!atEnd() && (Character.isLetterOrDigit(current()) || current() == '_')) {
            sb.append(advance());
        }

        String word = sb.toString();
        if (word.equals("true") || word.equals("false")) {
            return new Token(TokenType.BOOL_LITERAL, word, tokenLine);
        }
        if (word.equals("null")) {
            return new Token(TokenType.NULL_LITERAL, word, tokenLine);
        }
        if (KEYWORDS.contains(word)) {
            return new Token(TokenType.KEYWORD, word, tokenLine);
        }
        return new Token(TokenType.IDENTIFIER, word, tokenLine);
    }

    private boolean isStartOfMultiOp() {
        for (String op : MULTI_OPS) {
            if (src.startsWith(op, pos)) {
                return true;
            }
        }
        return false;
    }

    private Token readMultiOp(int tokenLine) {
        for (String op : MULTI_OPS) {
            if (src.startsWith(op, pos)) {
                pos += op.length();
                return new Token(TokenType.OPERATOR, op, tokenLine);
            }
        }
        return new Token(TokenType.OPERATOR, String.valueOf(advance()), tokenLine);
    }

    private boolean isHexDigit(char c) {
        return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    public static void main(String[] args) {

        // String filePath = "D:\\VS Code Works\\Java\\PL_Final
        // Project\\CSharpParser\\Scanner Module\\projectreq3_BOTIS_PADRINAO_input.cs";

        String source = "";
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(
                    "../projectreq3_BOTIS_PADRINAO_input.cs"));
            String ln;
            while ((ln = br.readLine()) != null) {
                sb.append(ln).append('\n');
            }
            br.close();
            source = sb.toString();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        projectreq3_BOTIS_PADRINAO scanner = new projectreq3_BOTIS_PADRINAO(source);
        List<Token> tokens = scanner.tokenise();

        System.out.println("=".repeat(55));
        System.out.printf("%-5s | %-20s | %s%n", "LINE", "TOKEN TYPE", "VALUE");
        System.out.println("=".repeat(55));

        boolean hasErrors = false;
        for (Token tok : tokens) {
            System.out.println(tok);
            if (tok.type == TokenType.ILLEGAL) {
                System.err.println("  >> LEXICAL ERROR at line " + tok.line + ": " + tok.value);
                hasErrors = true;
            }
        }

        System.out.println("=".repeat(55));
        System.out.println("Total tokens : " + tokens.size());
        System.out.println("Status       : " + (hasErrors ? "Completed with lexical errors." : "OK"));
    }
}