import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class projectreqt3_BOTIS_PADRINAO {
    private String source;
    private int cursor = 0;
    private int line = 1;
    private boolean hadError = false;

    // C# Keywords
    private static final List<String> KEYWORDS = Arrays.asList(
            "abstract", "as", "base", "bool", "break", "byte", "case", "catch",
            "char", "checked", "class", "const", "continue", "decimal", "default",
            "delegate", "do", "double", "else", "enum", "event", "explicit",
            "extern", "false", "finally", "fixed", "float", "for", "foreach",
            "goto", "if", "implicit", "in", "int", "interface", "internal", "is",
            "lock", "long", "namespace", "new", "null", "object", "operator",
            "out", "override", "params", "private", "protected", "public",
            "readonly", "ref", "return", "sbyte", "sealed", "short", "sizeof",
            "stackalloc", "static", "string", "struct", "switch", "this", "throw",
            "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe",
            "ushort", "using", "virtual", "void", "volatile", "while");

    public projectreqt3_BOTIS_PADRINAO(String source) {
        this.source = source;
    }

    public void scan() {
        System.out.println("--- STARTING SCAN ---");
        while (!isAtEnd()) {
            char c = advance();

            switch (c) {
                // Whitespace
                case ' ':
                case '\r':
                case '\t':
                    break;
                case '\n':
                    line++;
                    break;

                // Single-character Operators/Punctuation
                case '(':
                    addToken("LEFT_PAREN", "(");
                    break;
                case ')':
                    addToken("RIGHT_PAREN", ")");
                    break;
                case '{':
                    addToken("LEFT_BRACE", "{");
                    break;
                case '}':
                    addToken("RIGHT_BRACE", "}");
                    break;
                case '[':
                    addToken("LEFT_BRACKET", "[");
                    break;
                case ']':
                    addToken("RIGHT_BRACKET", "]");
                    break;
                case ',':
                    addToken("COMMA", ",");
                    break;
                case '.':
                    addToken("DOT", ".");
                    break;
                case ';':
                    addToken("SEMICOLON", ";");
                    break;
                case ':':
                    addToken("COLON", ":");
                    break;

                // Multi-character Operators
                case '+':
                    if (match('+'))
                        addToken("PLUS_PLUS", "++");
                    else
                        addToken("PLUS", "+");
                    break;
                case '-':
                    if (match('-'))
                        addToken("MINUS_MINUS", "--");
                    else
                        addToken("MINUS", "-");
                    break;
                case '*':
                    addToken("STAR", "*");
                    break;
                case '=': {
                    boolean isEq = match('=');
                    addToken(isEq ? "EQUAL_EQUAL" : "EQUAL", isEq ? "==" : "=");
                    break;
                }
                case '!': {
                    boolean isEq = match('=');
                    addToken(isEq ? "BANG_EQUAL" : "BANG", isEq ? "!=" : "!");
                    break;
                }
                case '<': {
                    boolean isEq = match('=');
                    addToken(isEq ? "LESS_EQUAL" : "LESS", isEq ? "<=" : "<");
                    break;
                }
                case '>': {
                    boolean isEq = match('=');
                    addToken(isEq ? "GREATER_EQUAL" : "GREATER", isEq ? ">=" : ">");
                    break;
                }
                case '&': {
                    if (match('&'))
                        addToken("LOGICAL_AND", "&&");
                    else
                        addToken("BITWISE_AND", "&");
                    break;
                }
                case '|': {
                    if (match('|'))
                        addToken("LOGICAL_OR", "||");
                    else
                        addToken("BITWISE_OR", "|");
                    break;
                }

                // Comments and Slash
                case '/':
                    if (match('/')) {
                        // Single line comment: Consume until end of line
                        while (peek() != '\n' && !isAtEnd())
                            advance();
                    } else if (match('*')) {
                        // Block comment
                        consumeBlockComment();
                    } else {
                        addToken("SLASH", "/");
                    }
                    break;

                // String Literals
                case '"':
                    consumeString();
                    break;

                default:
                    if (isDigit(c)) {
                        consumeNumber(c);
                    } else if (isAlpha(c)) {
                        consumeIdentifier(c);
                    } else {
                        System.err.println("[Error] Line " + line + ": Illegal character '" + c + "'");
                        System.err.println("--- SCAN HALTED DUE TO ERROR ---");
                        hadError = true;
                        return; // Halt on invalid sequence
                    }
                    break;
            }
        }

        if (!hadError) {
            System.out.println("--- SCAN COMPLETE ---");
        }
    }

    private void consumeString() {
        StringBuilder val = new StringBuilder();
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            val.append(advance());
        }

        if (isAtEnd()) {
            System.err.println("[Error] Line " + line + ": Unterminated string.");
            System.err.println("--- SCAN HALTED DUE TO ERROR ---");
            hadError = true;
            return;
        }

        advance(); // The closing "
        addToken("STRING_LITERAL", "\"" + val.toString() + "\"");
    }

    private void consumeBlockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance(); // consume '*'
                advance(); // consume '/'
                return;
            }
            if (peek() == '\n')
                line++;
            advance();
        }
        System.err.println("[Error] Line " + line + ": Unterminated block comment.");
        System.err.println("--- SCAN HALTED DUE TO ERROR ---");
        hadError = true;
    }

    private void consumeNumber(char firstDigit) {
        StringBuilder val = new StringBuilder();
        val.append(firstDigit);
        while (isDigit(peek()))
            val.append(advance());

        // Handle decimals
        if (peek() == '.' && isDigit(peekNext())) {
            val.append(advance()); // Consume '.'
            while (isDigit(peek()))
                val.append(advance());
        }
        addToken("NUMBER_LITERAL", val.toString());
    }

    private void consumeIdentifier(char firstChar) {
        StringBuilder val = new StringBuilder();
        val.append(firstChar);
        while (isAlphaNumeric(peek()))
            val.append(advance());

        String text = val.toString();
        String type = KEYWORDS.contains(text) ? "KEYWORD" : "IDENTIFIER";
        addToken(type, text);
    }

    // Helpers
    private boolean isAtEnd() {
        return cursor >= source.length() || hadError;
    }

    private char advance() {
        return source.charAt(cursor++);
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(cursor);
    }

    private char peekNext() {
        return (cursor + 1 >= source.length()) ? '\0' : source.charAt(cursor + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(cursor) != expected)
            return false;
        cursor++;
        return true;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void addToken(String type, String lexeme) {
        System.out.printf("[%s] : %s\n", type, lexeme);
    }

    public static void main(String[] args) {
        try {
            String content = Files.readString(Paths.get("../projectreqt3_SURNAME1_SURNAME2_input.cs"));
            projectreqt3_BOTIS_PADRINAO scanner = new projectreqt3_BOTIS_PADRINAO(content);
            scanner.scan();
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }
}