public class projectreq4_BOTIS {
    // BOTIS' INITIAL OUTLINE AND CODE PLANNING WITH HELP FROM AI

    // Task 1 - Data Structure Selection
    // Define a collection to store the C# reserved keywords.
    // Use a HashSet<String> for O(1) constant time lookup performance.
    // Reference: C# has roughly 70+ reserved keywords (e.g., "abstract", "as",
    // "base").

    // Task 2 - Initialization
    // Create a constructor or a static block to populate the collection.
    // Ensure you include:
    // - Types: int, string, bool, char, decimal, etc.
    // - Control Flow: if, else, switch, case, while, do, for, foreach.
    // - Modifiers: public, private, protected, internal, static, readonly.
    // - Logic: true, false, null.
    // - Contextual Keywords (Optional but recommended): get, set, yield, var.

    /**
     * Task 3 - Lookup Logic
     * Create a method (e.g., getTokenType or isKeyword) that accepts a String
     * lexeme.
     * * Logic:
     * 1. Accept 'lexeme' from the Scanner.
     * 2. Check if the lexeme exists in the Keyword HashSet.
     * 3. Return a specific Type/Enum (e.g., TokenType.KEYWORD_IF) or a boolean.
     */

    // Task 4 - Case Sensitivity
    // Note: C# is case-sensitive.
    // Ensure "While" is treated as an Identifier, but "while" is a Keyword.
    // Do not lowercase the input before checking
}
