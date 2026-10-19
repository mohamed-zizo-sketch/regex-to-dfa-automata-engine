package toc;

/**
 * Converts tokenized infix regular expressions into postfix (Reverse Polish) notation
 * using Dijkstra's Shunting-yard algorithm adapted for regular expression operators.
 */
public class InfixToPostfix {

    public static boolean isOperator(String s) {
        return s.equals("|") ||
               s.equals(".") ||
               s.equals("*") ||
               s.equals("+") ||
               s.equals("?");
    }

    public static boolean isSymbol(String s) {
        return !isOperator(s) && !s.equals("(") && !s.equals(")");
    }

    public static int precedence(String s) {
        if (s.equals("*") || s.equals("+") || s.equals("?")) return 3; // Unary repetition / optional
        if (s.equals(".")) return 2;                                    // Binary concatenation
        if (s.equals("|")) return 1;                                    // Binary union / alternation
        return 0;
    }

    public static queue convert(queue infix) {
        stack tempStack = new stack();
        queue postfix = new queue();

        while (!infix.isEmpty()) {
            String token = infix.dequeue();

            if (isSymbol(token)) {
                postfix.enqueue(token);
            } else if (token.equals("(")) {
                tempStack.push(token);
            } else if (token.equals(")")) {
                while (!tempStack.IsEmpty() && !tempStack.checkTop().equals("(")) {
                    postfix.enqueue(tempStack.pop());
                }
                if (!tempStack.IsEmpty() && tempStack.checkTop().equals("(")) {
                    tempStack.pop(); // Discard '('
                } else {
                    throw new IllegalArgumentException("Mismatched parentheses: unexpected ')'");
                }
            } else if (token.equals("*") || token.equals("+") || token.equals("?")) {
                // Unary operators have highest precedence and bind immediately
                postfix.enqueue(token);
            } else if (isOperator(token)) {
                // Binary operators (. and |)
                while (!tempStack.IsEmpty() && precedence(tempStack.checkTop()) >= precedence(token)) {
                    postfix.enqueue(tempStack.pop());
                }
                tempStack.push(token);
            }
        }

        while (!tempStack.IsEmpty()) {
            String op = tempStack.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses: unclosed '('");
            }
            postfix.enqueue(op);
        }

        return postfix;
    }
}
