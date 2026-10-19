package toc;

import java.util.*;

/**
 * Manages the construction, alphabet extraction, and inspection of NFAs
 * generated from regular expressions via Thompson's Construction.
 */
public class RegexToNFAManager {

    /**
     * Builds an NFA from a postfix expression string or token queue.
     * Supports:
     * - Literals (alphanumeric characters)
     * - Concatenation ('.')
     * - Alternation / Union ('|')
     * - Kleene Star ('*')
     * - Positive Closure ('+')
     * - Optional ('?')
     */
    public static NFA buildNFA(String postfix) {
        State.resetIdCounter();
        Stack<NFA> stack = new Stack<>();

        String[] tokens = postfix.trim().split("\\s+");
        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (token.length() == 1) {
                char c = token.charAt(0);
                if (c == '*') {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid postfix: missing operand for unary '*'");
                    }
                    stack.push(NFA.star(stack.pop()));
                } else if (c == '+') {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid postfix: missing operand for unary '+'");
                    }
                    stack.push(NFA.plus(stack.pop()));
                } else if (c == '?') {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid postfix: missing operand for unary '?'");
                    }
                    stack.push(NFA.optional(stack.pop()));
                } else if (c == '|') {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Invalid postfix: missing operands for union '|'");
                    }
                    NFA b = stack.pop();
                    NFA a = stack.pop();
                    stack.push(NFA.union(a, b));
                } else if (c == '.') {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Invalid postfix: missing operands for concatenation '.'");
                    }
                    NFA b = stack.pop();
                    NFA a = stack.pop();
                    stack.push(NFA.concat(a, b));
                } else {
                    // Single terminal symbol
                    stack.push(NFA.fromSymbol(c));
                }
            } else {
                // Multi-character literal token
                for (char c : token.toCharArray()) {
                    stack.push(NFA.fromSymbol(c));
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression: could not reduce to a single root NFA");
        }
        return stack.pop();
    }

    /**
     * Dynamically extracts all distinct terminal alphabet symbols from the postfix expression.
     */
    public static List<Character> extractAlphabet(String postfix) {
        Set<Character> alphabetSet = new TreeSet<>();
        String[] tokens = postfix.trim().split("\\s+");
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            for (char c : token.toCharArray()) {
                if (c != '*' && c != '+' && c != '?' && c != '|' && c != '.' && c != '(' && c != ')' && c != ' ') {
                    alphabetSet.add(c);
                }
            }
        }
        return new ArrayList<>(alphabetSet);
    }

    /**
     * Recursively displays NFA transitions in a readable ASCII format.
     */
    public static void printNFA(State state, Set<Integer> visited) {
        if (state == null || visited.contains(state.id)) return;
        visited.add(state.id);

        for (Map.Entry<Character, List<State>> entry : state.transitions.entrySet()) {
            for (State next : entry.getValue()) {
                System.out.printf("  q%-3d -----( %s )-----> q%-3d\n", state.id, entry.getKey(), next.id);
                printNFA(next, visited);
            }
        }

        for (State next : state.epsilonTransitions) {
            System.out.printf("  q%-3d -----( ε )-----> q%-3d\n", state.id, next.id);
            printNFA(next, visited);
        }
    }

    /**
     * Generates a Mermaid state diagram for the NFA.
     */
    public static String toMermaid(NFA nfa) {
        StringBuilder sb = new StringBuilder();
        sb.append("```mermaid\n");
        sb.append("stateDiagram-v2\n");
        sb.append("    [*] --> q").append(nfa.start.id).append("\n");

        Set<Integer> visited = new HashSet<>();
        Queue<State> q = new LinkedList<>();
        q.add(nfa.start);
        visited.add(nfa.start.id);

        while (!q.isEmpty()) {
            State curr = q.poll();
            if (curr.isFinal) {
                sb.append("    q").append(curr.id).append(" --> [*]\n");
            }
            for (Map.Entry<Character, List<State>> entry : curr.transitions.entrySet()) {
                for (State next : entry.getValue()) {
                    sb.append("    q").append(curr.id).append(" --> q").append(next.id)
                      .append(" : ").append(entry.getKey()).append("\n");
                    if (!visited.contains(next.id)) {
                        visited.add(next.id);
                        q.add(next);
                    }
                }
            }
            for (State next : curr.epsilonTransitions) {
                sb.append("    q").append(curr.id).append(" --> q").append(next.id)
                  .append(" : ε\n");
                if (!visited.contains(next.id)) {
                    visited.add(next.id);
                    q.add(next);
                }
            }
        }
        sb.append("```\n");
        return sb.toString();
    }
}
