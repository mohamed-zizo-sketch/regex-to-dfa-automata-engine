package toc;

import java.util.*;

/**
 * Main application entry point for the Theory of Computation (TOC)
 * Regular Expression to NFA & DFA Engine.
 *
 * Demonstrates:
 * 1. Lexical Tokenization with Implicit Concatenation
 * 2. Infix to Postfix Conversion (Shunting-yard Algorithm)
 * 3. Thompson's Construction (Regex -> NFA with epsilon transitions)
 * 4. Subset Construction (NFA -> DFA with epsilon-closure)
 * 5. Deterministic String Matching & Simulation
 * 6. Mermaid State Diagram Generation
 */
public class Toc {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================================");
        System.out.println("   THEORY OF COMPUTATION: REGEX TO NFA & DFA AUTOMATA ENGINE      ");
        System.out.println("==================================================================");

        // If an expression is passed via command-line arguments, process it directly
        if (args != null && args.length > 0 && !args[0].trim().isEmpty()) {
            processRegex(args[0].trim(), scanner);
            return;
        }

        boolean running = true;
        while (running && scanner.hasNextLine()) {
            System.out.println("\n[MENU]");
            System.out.println("  1. Analyze Custom Regular Expression");
            System.out.println("  2. Run Automated Test Suite (Self-Verification)");
            System.out.println("  3. Exit");
            System.out.print("\nSelect an option (1-3): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("\nEnter Regular Expression (e.g. (a|b)*abb, a+b?, (0|1)*1): ");
                    String regex = scanner.nextLine().trim();
                    if (regex.isEmpty()) {
                        System.out.println("No expression entered. Returning to menu.");
                        break;
                    }
                    processRegex(regex, scanner);
                    break;

                case "2":
                    runTestSuite();
                    break;

                case "3":
                    System.out.println("Exiting Automata Engine. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid selection. Please choose 1, 2, or 3.");
                    break;
            }
        }
    }

    /**
     * Executes the complete pipeline for a given regular expression.
     */
    public static void processRegex(String regex, Scanner scanner) {
        try {
            System.out.println("\n==================================================================");
            System.out.println("Processing Regular Expression: \"" + regex + "\"");
            System.out.println("==================================================================");

            // 1. Tokenization
            Tokenizer tokenizer = new Tokenizer();
            queue infix = tokenizer.tokenize(regex);

            System.out.print("\n[Step 1] Infix Token Queue  : ");
            infix.printQueue();

            // 2. Infix to Postfix
            queue postfix = InfixToPostfix.convert(infix);
            System.out.print("[Step 2] Postfix Token Queue: ");
            postfix.printQueue();

            String postfixStr = postfix.toExpressionString();
            System.out.println("         Postfix String     : " + postfixStr);

            // 3. Dynamic Alphabet Extraction
            List<Character> alphabet = RegexToNFAManager.extractAlphabet(postfixStr);
            System.out.println("[Step 3] Active Alphabet (Σ): " + alphabet);

            // 4. Thompson's Construction: Postfix -> NFA
            System.out.println("\n[Step 4] Thompson's Construction (NFA)");
            NFA nfa = RegexToNFAManager.buildNFA(postfixStr);
            System.out.println("  Start State: q" + nfa.start.id);
            System.out.println("  Final State: q" + nfa.end.id);
            System.out.println("\n--- NFA Transitions ---");
            RegexToNFAManager.printNFA(nfa.start, new HashSet<>());

            // 5. Subset Construction: NFA -> DFA
            System.out.println("\n[Step 5] Subset Construction (NFA -> DFA)");
            DFA dfa = NFAToDFAConverter.convert(nfa, alphabet);
            dfa.printTransitionTable();

            // 6. Interactive String Matching Simulation
            System.out.println("\n[Step 6] Test Strings Against DFA");
            System.out.println("Enter test strings one by one (or press ENTER on an empty line to finish):");
            while (scanner.hasNextLine()) {
                System.out.print("Test String > ");
                String testStr = scanner.nextLine();
                if (testStr.isEmpty()) {
                    break;
                }
                dfa.simulate(testStr);
            }

            // 7. Optional Visual Export
            if (scanner.hasNextLine()) {
                System.out.print("\nDisplay Mermaid diagrams for NFA and DFA? (y/N): ");
                String showDiag = scanner.nextLine().trim();
                if (showDiag.equalsIgnoreCase("y")) {
                    System.out.println("\n--- Mermaid NFA Diagram ---");
                    System.out.println(RegexToNFAManager.toMermaid(nfa));
                    System.out.println("\n--- Mermaid DFA Diagram ---");
                    System.out.println(dfa.toMermaid());
                }
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Built-in automated verification test suite.
     */
    public static void runTestSuite() {
        System.out.println("\n==================================================================");
        System.out.println("              RUNNING AUTOMATED VERIFICATION SUITE                ");
        System.out.println("==================================================================");

        AutomataTestSuite.runAllTests();
    }
}
