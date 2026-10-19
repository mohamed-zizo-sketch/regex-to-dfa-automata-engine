package toc;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Performs lexical analysis on regular expressions.
 * Breaks down regex strings into individual tokens and injects explicit
 * concatenation operators ('.') between adjacent matching expressions.
 */
public class Tokenizer {

    /**
     * Determines whether an explicit concatenation operator ('.') should be inserted
     * between token t1 and token t2.
     *
     * Concatenation is needed when:
     * - t1 is an alphanumeric symbol, ')', or a unary operator ('*', '+', '?')
     * AND
     * - t2 is an alphanumeric symbol or '('
     */
    public boolean needsConcat(String t1, String t2) {
        if (t1 == null || t1.isEmpty() || t2 == null || t2.isEmpty()) return false;
        char c1 = t1.charAt(0);
        char c2 = t2.charAt(0);

        boolean t1CanEnd = Character.isLetterOrDigit(c1) || t1.equals(")") || t1.equals("*") || t1.equals("+") || t1.equals("?");
        boolean t2CanStart = Character.isLetterOrDigit(c2) || t2.equals("(");

        return t1CanEnd && t2CanStart;
    }

    /**
     * Tokenizes a given regular expression string.
     */
    public queue tokenize(String ex) {
        if (ex == null || ex.trim().isEmpty()) {
            return new queue();
        }

        ArrayList<String> tokens = new ArrayList<>();
        int i = 0;

        while (i < ex.length()) {
            char ch = ex.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            // Parentheses
            if (ch == '(' || ch == ')') {
                tokens.add("" + ch);
            }
            // Operators: alternation, star, plus, optional, explicit concat
            else if (ch == '|' || ch == '*' || ch == '+' || ch == '?' || ch == '.') {
                tokens.add("" + ch);
            }
            // Alphanumeric literals
            else if (Character.isLetterOrDigit(ch)) {
                tokens.add("" + ch);
            }
            // Unknown or unsupported character
            else {
                System.err.println("Lexical Warning: Unexpected symbol '" + ch + "'. Treating as literal.");
                tokens.add("" + ch);
            }

            i++;
        }

        // Insert implicit concatenation operators ('.')
        ArrayList<String> finalTokens = new ArrayList<>();
        for (int j = 0; j < tokens.size(); j++) {
            String t1 = tokens.get(j);
            finalTokens.add(t1);

            if (j + 1 < tokens.size()) {
                String t2 = tokens.get(j + 1);
                if (needsConcat(t1, t2)) {
                    finalTokens.add(".");
                }
            }
        }

        // Transfer into token Queue
        queue q = new queue();
        for (String t : finalTokens) {
            q.enqueue(t);
        }

        return q;
    }

    /**
     * Reads regex input interactively from standard input.
     */
    public queue tokenizer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Regular Expression: ");
        String ex = sc.nextLine();
        return tokenize(ex);
    }
}