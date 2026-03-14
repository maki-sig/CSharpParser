import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class projectreq3_BOTIS_PADRINAO {
    private String source;
    private List<String> tokens = new ArrayList<>();

    // C# Token Definitions
    private static final String KEYWORDS = "\\b(abstract|as|base|bool|break|byte|case|catch|char|checked|class|const|continue|decimal|default|delegate|do|double|else|enum|event|explicit|extern|false|finally|fixed|float|for|foreach|goto|if|implicit|in|int|interface|internal|is|lock|long|namespace|new|null|object|operator|out|override|params|private|protected|public|readonly|ref|return|sbyte|sealed|short|sizeof|stackalloc|static|string|struct|switch|this|throw|true|try|typeof|uint|ulong|unchecked|unsafe|ushort|using|static|virtual|void|volatile|while)\\b";
    private static final String IDENTIFIER = "[a-zA-Z_][a-zA-Z0-9_]*";
    private static final String NUMBER = "\\b\\d+(\\.\\d+)?\\b";
    private static final String STRING_LITERAL = "\"(?:\\\\.|[^\"\\\\])*\"";
    private static final String OPERATORS = "==|!=|<=|>=|&&|\\|\\||\\+\\+|--|=>|\\+|-|\\*|/|%|=|!|<|>";
    private static final String PUNCTUATION = "[\\{\\}\\(\\)\\[\\];,\\.]";

    // Combined Pattern (Matches comments first to discard them)
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            String.format(
                    "(?<COMMENT>//.*|/\\*[\\s\\S]*?\\*/)|(?<STRING>%s)|(?<KEYWORD>%s)|(?<ID>%s)|(?<NUM>%s)|(?<OP>%s)|(?<PUNC>%s)",
                    STRING_LITERAL, KEYWORDS, IDENTIFIER, NUMBER, OPERATORS, PUNCTUATION));

    public projectreq3_BOTIS_PADRINAO(String source) {
        this.source = source;
    }

    public void scan() {
        Matcher matcher = TOKEN_PATTERN.matcher(source);
        while (matcher.find()) {
            // If it's not a comment, add it to our lexeme stream
            if (matcher.group("COMMENT") == null) {
                tokens.add(matcher.group().trim());
            }
        }
    }

    public void displayTokens() {
        System.out.println("--- Lexeme Stream ---");
        for (String token : tokens) {
            System.out.println("[" + token + "]");
        }
    }

    public static void main(String[] args) {
        // Example usage (In production, read from the input file)
        String code = "int x = 10; /* Block \n Comment */ string msg = \"Hello World\"; x++;";
        projectreq3_BOTIS_PADRINAO sc = new projectreq3_BOTIS_PADRINAO(code);
        sc.scan();
        sc.displayTokens();
    }
}