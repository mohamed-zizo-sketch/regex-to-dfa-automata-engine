package toc;

import java.util.*;

/**
 * Models a Non-deterministic Finite Automaton (NFA) fragment with a single
 * entry start state and a single exit end state.
 *
 * Implements Thompson's Construction primitives:
 * - Basic Symbol: (s) --c--> (e)
 * - Epsilon: (s) --ε--> (e)
 * - Concatenation: A . B
 * - Alternation / Union: A | B
 * - Kleene Star: A* (0 or more)
 * - Positive Closure: A+ (1 or more)
 * - Optional: A? (0 or 1)
 */
public class NFA {
    public State start;
    public State end;

    public NFA(State start, State end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Builds an NFA matching a single symbol: (s) --c--> (e*)
     */
    public static NFA fromSymbol(char c) {
        State s = new State();
        State e = new State();
        e.isFinal = true;
        s.addTransition(c, e);
        return new NFA(s, e);
    }

    /**
     * Builds an NFA matching the empty string: (s) --ε--> (e*)
     */
    public static NFA fromEpsilon() {
        State s = new State();
        State e = new State();
        e.isFinal = true;
        s.addEpsilonTransition(e);
        return new NFA(s, e);
    }

    /**
     * Concatenation: A . B
     * Links a.end to b.start with an epsilon transition.
     */
    public static NFA concat(NFA a, NFA b) {
        a.end.isFinal = false;
        a.end.addEpsilonTransition(b.start);
        return new NFA(a.start, b.end);
    }

    /**
     * Alternation / Union: A | B
     * Creates new start and end states branching into 'a' and 'b'.
     */
    public static NFA union(NFA a, NFA b) {
        State s = new State();
        State e = new State();
        s.addEpsilonTransition(a.start);
        s.addEpsilonTransition(b.start);
        a.end.isFinal = false;
        b.end.isFinal = false;
        a.end.addEpsilonTransition(e);
        b.end.addEpsilonTransition(e);
        e.isFinal = true;
        return new NFA(s, e);
    }

    /**
     * Kleene Star: A* (0 or more occurrences)
     */
    public static NFA star(NFA a) {
        State s = new State();
        State e = new State();
        s.addEpsilonTransition(a.start);
        s.addEpsilonTransition(e); // Skip over for zero matches
        a.end.isFinal = false;
        a.end.addEpsilonTransition(a.start); // Loop back for repeat matches
        a.end.addEpsilonTransition(e);
        e.isFinal = true;
        return new NFA(s, e);
    }

    /**
     * Positive Closure: A+ (1 or more occurrences: A . A*)
     */
    public static NFA plus(NFA a) {
        State s = new State();
        State e = new State();
        s.addEpsilonTransition(a.start);
        a.end.isFinal = false;
        a.end.addEpsilonTransition(a.start); // Loop back for repeat matches
        a.end.addEpsilonTransition(e);
        e.isFinal = true;
        return new NFA(s, e);
    }

    /**
     * Optional: A? (0 or 1 occurrence)
     */
    public static NFA optional(NFA a) {
        State s = new State();
        State e = new State();
        s.addEpsilonTransition(a.start);
        s.addEpsilonTransition(e); // Skip over for zero matches
        a.end.isFinal = false;
        a.end.addEpsilonTransition(e);
        e.isFinal = true;
        return new NFA(s, e);
    }
}