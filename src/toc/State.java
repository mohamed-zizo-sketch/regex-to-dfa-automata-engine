package toc;

import java.util.*;

/**
 * Represents an individual state in a Non-deterministic Finite Automaton (NFA).
 * Each state possesses a unique numeric identifier, symbol-based transitions,
 * epsilon transitions, and an accepting (final) flag.
 */
public class State {
    public static int idCounter = 0;

    public final int id;
    public final Map<Character, List<State>> transitions;
    public final List<State> epsilonTransitions;
    public boolean isFinal;

    public State() {
        this.id = idCounter++;
        this.transitions = new HashMap<>();
        this.epsilonTransitions = new ArrayList<>();
        this.isFinal = false;
    }

    /**
     * Resets the static state ID counter back to zero.
     * Useful when building new automata within the same process.
     */
    public static void resetIdCounter() {
        idCounter = 0;
    }

    /**
     * Adds a transition on an input symbol to a target state.
     */
    public void addTransition(char symbol, State next) {
        transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(next);
    }

    /**
     * Adds an epsilon (empty string) transition to a target state.
     */
    public void addEpsilonTransition(State next) {
        epsilonTransitions.add(next);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id == state.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "q" + id + (isFinal ? "*" : "");
    }
}
