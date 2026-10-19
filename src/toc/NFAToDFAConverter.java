package toc;

import java.util.*;

/**
 * Converts a Non-deterministic Finite Automaton (NFA) into an equivalent
 * Deterministic Finite Automaton (DFA) using the Subset Construction algorithm.
 */
public class NFAToDFAConverter {

    /**
     * Computes the epsilon-closure (all states reachable via zero or more epsilon transitions)
     * of a given set of NFA states.
     */
    public static Set<State> getEpsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            State current = stack.pop();
            for (State next : current.epsilonTransitions) {
                if (!closure.contains(next)) {
                    closure.add(next);
                    stack.push(next);
                }
            }
        }
        return closure;
    }

    /**
     * Computes the set of states reachable by consuming a specific symbol
     * from any state in the given set.
     */
    public static Set<State> move(Set<State> states, char symbol) {
        Set<State> result = new HashSet<>();
        for (State state : states) {
            if (state.transitions.containsKey(symbol)) {
                result.addAll(state.transitions.get(symbol));
            }
        }
        return result;
    }

    /**
     * Converts an NFA into an equivalent DFA using Subset Construction.
     */
    public static DFA convert(NFA nfa, List<Character> alphabet) {
        Set<State> startSet = new HashSet<>();
        startSet.add(nfa.start);
        Set<State> startClosure = getEpsilonClosure(startSet);

        Map<Set<State>, DFAState> dfaMap = new HashMap<>();
        List<DFAState> dfaStatesList = new ArrayList<>();
        Queue<Set<State>> unprocessed = new LinkedList<>();

        int stateCounter = 0;
        DFAState startDFAState = new DFAState("S" + stateCounter++, startClosure);
        dfaMap.put(startClosure, startDFAState);
        dfaStatesList.add(startDFAState);
        unprocessed.add(startClosure);

        while (!unprocessed.isEmpty()) {
            Set<State> currentSubset = unprocessed.poll();
            DFAState currentDFA = dfaMap.get(currentSubset);

            for (char symbol : alphabet) {
                Set<State> moveResult = move(currentSubset, symbol);
                if (moveResult.isEmpty()) continue;

                Set<State> nextClosure = getEpsilonClosure(moveResult);
                if (nextClosure.isEmpty()) continue;

                DFAState targetDFA = dfaMap.get(nextClosure);
                if (targetDFA == null) {
                    targetDFA = new DFAState("S" + stateCounter++, nextClosure);
                    dfaMap.put(nextClosure, targetDFA);
                    dfaStatesList.add(targetDFA);
                    unprocessed.add(nextClosure);
                }
                currentDFA.addTransition(symbol, targetDFA);
            }
        }

        return new DFA(startDFAState, dfaStatesList, alphabet);
    }

    /**
     * Backward-compatibility bridge for older test harnesses.
     */
    public static DFA convertNFAtoDFA(Set<State> startNFAStates, char[] alphabet) {
        List<Character> alphaList = new ArrayList<>();
        for (char c : alphabet) alphaList.add(c);

        State dummyStart = new State();
        for (State s : startNFAStates) {
            dummyStart.addEpsilonTransition(s);
        }
        NFA dummyNfa = new NFA(dummyStart, null);
        DFA dfa = convert(dummyNfa, alphaList);
        dfa.printTransitionTable();
        return dfa;
    }
}
