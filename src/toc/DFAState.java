package toc;

import java.util.*;

/**
 * Represents a state in a Deterministic Finite Automaton (DFA).
 * Each DFA state corresponds to a unique subset of NFA states (computed via epsilon-closure).
 */
public class DFAState {
    public final String name;                // e.g. "S0", "S1", "S2"
    public final Set<State> nfaStates;       // Corresponding subset of NFA states
    public final boolean isAccepting;        // True if any state in nfaStates is final
    public final Map<Character, DFAState> transitions;

    public DFAState(String name, Set<State> nfaStates) {
        this.name = name;
        this.nfaStates = Collections.unmodifiableSet(new HashSet<>(nfaStates));
        this.isAccepting = nfaStates.stream().anyMatch(s -> s.isFinal);
        this.transitions = new HashMap<>();
    }

    public void addTransition(char symbol, DFAState target) {
        transitions.put(symbol, target);
    }

    /**
     * Returns a sorted string representation of the NFA state IDs in this subset.
     */
    public String getNfaStateIds() {
        List<Integer> ids = new ArrayList<>();
        for (State s : nfaStates) {
            ids.add(s.id);
        }
        Collections.sort(ids);
        return ids.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAState dfaState = (DFAState) o;
        return Objects.equals(name, dfaState.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return name + (isAccepting ? "*" : "") + " " + getNfaStateIds();
    }
}
