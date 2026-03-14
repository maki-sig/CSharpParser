import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class CSharpScanner {
    private String source;
    private int cursor = 0;
    private int line = 1;

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

    public CSharpScanner(String source) {
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
                    addToken("PLUS", "+");
                    break;
                case '-':
                    addToken("MINUS", "-");
                    break;
                case '*':
                    addToken("STAR", "*");
                    break;
                case '=':
                    addToken(match('=') ? "EQUAL_EQUAL" : "EQUAL", match('=') ? "==" : "=");
                    break;
                case '!':
                    addToken(match('=') ? "BANG_EQUAL" : "BANG", match('=') ? "!=" : "!");
                    break;
                case '<':
                    addToken(match('=') ? "LESS_EQUAL" : "LESS", match('=') ? "<=" : "<");
                    break;
                case '>':
                    addToken(match('=') ? "GREATER_EQUAL" : "GREATER", match('=') ? ">=" : ">");
                    break;

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
                        consumeNumber();
                    } else if (isAlpha(c)) {
                        consumeIdentifier();
                    } else {
                        System.err.println("[Error] Line " + line + ": Illegal character '" + c + "'");
                    }
                    break;
            }
        }
        System.out.println("--- SCAN COMPLETE ---");
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
            return;
        }

        advance(); // The closing "
        addToken("STRING_LITERAL", val.toString());
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
    }

    private void consumeNumber() {
        StringBuilder val = new StringBuilder();
        val.append(source.charAt(cursor - 1));
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

    private void consumeIdentifier() {
        StringBuilder val = new StringBuilder();
        val.append(source.charAt(cursor - 1));
        while (isAlphaNumeric(peek()))
            val.append(advance());

        String text = val.toString();
        String type = KEYWORDS.contains(text) ? "KEYWORD" : "IDENTIFIER";
        addToken(type, text);
    }

    // Helpers
    private boolean isAtEnd() {
        return cursor >= source.length();
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
            // Replace with your actual path
            String content = Files.readString(Paths.get("projectreqt3_SURNAME1_SURNAME2_input.cs"));
            CSharpScanner scanner = new CSharpScanner(content);
            scanner.scan();
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }
}