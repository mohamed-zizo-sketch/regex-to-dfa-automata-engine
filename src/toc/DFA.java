package toc;

import java.util.*;

/**
 * Represents a Deterministic Finite Automaton (DFA) constructed via Subset Construction.
 * Provides capabilities for string matching simulation, ASCII transition table formatting,
 * and diagram export.
 */
public class DFA {
    public final DFAState startState;
    public final List<DFAState> states;
    public final List<Character> alphabet;

    public DFA(DFAState startState, List<DFAState> states, List<Character> alphabet) {
        this.startState = startState;
        this.states = states;
        this.alphabet = alphabet;
    }

    /**
     * Simulates the DFA execution on a candidate string.
     * Traces state transitions step-by-step and returns whether the input is accepted.
     */
    public boolean simulate(String input) {
        System.out.println("\n  --- Simulating String: \"" + input + "\" ---");
        DFAState current = startState;
        System.out.printf("  Initial State: %s %s\n", current.name, current.isAccepting ? "(Accepting)" : "");

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            DFAState next = current.transitions.get(ch);

            if (next == null) {
                System.out.printf("  Step %d: '%c' -> [NO TRANSITION] (Trapped / Dead)\n", i + 1, ch);
                System.out.println("  ==> Match Result: REJECTED ❌");
                return false;
            }

            System.out.printf("  Step %d: '%c' -> %s %s\n", i + 1, ch, next.name, next.isAccepting ? "(Accepting)" : "");
            current = next;
        }

        boolean accepted = current.isAccepting;
        if (accepted) {
            System.out.println("  ==> Match Result: ACCEPTED ✅ (Halted at accepting state " + current.name + ")");
        } else {
            System.out.println("  ==> Match Result: REJECTED ❌ (Halted at non-accepting state " + current.name + ")");
        }
        return accepted;
    }

    /**
     * Checks if a string matches the regular expression without printing trace logs.
     */
    public boolean matches(String input) {
        DFAState current = startState;
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            DFAState next = current.transitions.get(ch);
            if (next == null) return false;
            current = next;
        }
        return current.isAccepting;
    }

    /**
     * Prints an ASCII transition table representing this DFA.
     */
    public void printTransitionTable() {
        System.out.println("\n+-------------------------------------------------------------------------+");
        System.out.println("|                           DFA TRANSITION TABLE                          |");
        System.out.println("+-------------------------------------------------------------------------+");

        // Table header
        System.out.printf("| %-12s | %-18s |", "DFA State", "NFA Subset");
        for (char symbol : alphabet) {
            System.out.printf(" %-5s |", "'" + symbol + "'");
        }
        System.out.printf(" %-10s |\n", "Accepting?");

        // Separator
        System.out.print("+--------------+--------------------+");
        for (int i = 0; i < alphabet.size(); i++) {
            System.out.print("-------+");
        }
        System.out.println("------------+");

        // Table rows
        for (DFAState s : states) {
            String prefix = (s == startState ? "-> " : "   ") + (s.isAccepting ? "* " : "  ");
            String stateCol = prefix + s.name;
            System.out.printf("| %-12s | %-18s |", stateCol, s.getNfaStateIds());

            for (char symbol : alphabet) {
                DFAState target = s.transitions.get(symbol);
                String targetName = (target != null) ? target.name : "-";
                System.out.printf(" %-5s |", targetName);
            }
            System.out.printf(" %-10s |\n", s.isAccepting ? "YES" : "no");
        }

        // Footer separator
        System.out.print("+--------------+--------------------+");
        for (int i = 0; i < alphabet.size(); i++) {
            System.out.print("-------+");
        }
        System.out.println("------------+");
        System.out.println("Legend: '->' = Start State, '*' = Accepting State, '-' = Reject / Dead State");
    }

    /**
     * Generates a Mermaid state diagram string for the DFA.
     */
    public String toMermaid() {
        StringBuilder sb = new StringBuilder();
        sb.append("```mermaid\n");
        sb.append("stateDiagram-v2\n");
        sb.append("    [*] --> ").append(startState.name).append("\n");

        for (DFAState s : states) {
            if (s.isAccepting) {
                sb.append("    ").append(s.name).append(" --> [*]\n");
            }
            for (Map.Entry<Character, DFAState> entry : s.transitions.entrySet()) {
                sb.append("    ").append(s.name).append(" --> ")
                  .append(entry.getValue().name).append(" : ").append(entry.getKey()).append("\n");
            }
        }
        sb.append("```\n");
        return sb.toString();
    }
}
