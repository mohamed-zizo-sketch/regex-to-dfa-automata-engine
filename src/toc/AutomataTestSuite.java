package toc;

import java.util.*;

/**
 * Automated verification test suite for the TOC Automata Engine.
 * Verifies end-to-end correctness of tokenization, infix-to-postfix conversion,
 * Thompson's NFA construction, Subset Construction DFA conversion, and string matching.
 */
public class AutomataTestSuite {

    public static void runAllTests() {
        int passed = 0;
        int failed = 0;

        System.out.println("Executing automated test suite...\n");

        // Test Case 1: Classic string ending in 'abb': (a|b)*abb
        if (testRegex("(a|b)*abb",
                Arrays.asList("abb", "ababb", "babb", "bbbbabb"),
                Arrays.asList("ab", "abba", "b", "a", ""))) {
            passed++;
        } else {
            failed++;
        }

        // Test Case 2: Binary strings ending in 1: (0|1)*1
        if (testRegex("(0|1)*1",
                Arrays.asList("1", "101", "001", "1111", "010101"),
                Arrays.asList("0", "10", "110", "", "000"))) {
            passed++;
        } else {
            failed++;
        }

        // Test Case 3: Positive closure & Kleene star: a*b+
        if (testRegex("a*b+",
                Arrays.asList("b", "bb", "ab", "aab", "aaabbbb"),
                Arrays.asList("a", "aaa", "", "ba", "aba"))) {
            passed++;
        } else {
            failed++;
        }

        // Test Case 4: Optional operator: (cat|dog)?
        if (testRegex("(cat|dog)?",
                Arrays.asList("", "cat", "dog"),
                Arrays.asList("ca", "do", "catdog", "dogcat", "bird"))) {
            passed++;
        } else {
            failed++;
        }

        // Test Case 5: Complex union and alternation: a(b|c)*d
        if (testRegex("a(b|c)*d",
                Arrays.asList("ad", "abd", "acd", "abbbcd", "acbcbcd"),
                Arrays.asList("a", "d", "abcdb", "abc", ""))) {
            passed++;
        } else {
            failed++;
        }

        System.out.println("\n--------------------------------------------------");
        System.out.printf("Test Results: %d Passed, %d Failed (Total %d)\n", passed, failed, (passed + failed));
        if (failed == 0) {
            System.out.println("STATUS: ALL TESTS PASSED! 🎉");
        } else {
            System.out.println("STATUS: SOME TESTS FAILED! ⚠️");
        }
        System.out.println("--------------------------------------------------");
    }

    private static boolean testRegex(String regex, List<String> shouldAccept, List<String> shouldReject) {
        System.out.println("Testing Regex: \"" + regex + "\"");
        try {
            Tokenizer tokenizer = new Tokenizer();
            queue infix = tokenizer.tokenize(regex);
            queue postfix = InfixToPostfix.convert(infix);
            String postfixStr = postfix.toExpressionString();

            List<Character> alphabet = RegexToNFAManager.extractAlphabet(postfixStr);
            NFA nfa = RegexToNFAManager.buildNFA(postfixStr);
            DFA dfa = NFAToDFAConverter.convert(nfa, alphabet);

            boolean casePassed = true;

            // Verify accepting strings
            for (String str : shouldAccept) {
                boolean accepted = dfa.matches(str);
                if (!accepted) {
                    System.err.printf("  FAIL: Expected \"%s\" to be ACCEPTED, but was REJECTED.\n", str);
                    casePassed = false;
                }
            }

            // Verify rejecting strings
            for (String str : shouldReject) {
                boolean accepted = dfa.matches(str);
                if (accepted) {
                    System.err.printf("  FAIL: Expected \"%s\" to be REJECTED, but was ACCEPTED.\n", str);
                    casePassed = false;
                }
            }

            if (casePassed) {
                System.out.println("  ==> PASSED (All positive & negative matches verified) ✅");
            }
            return casePassed;

        } catch (Exception e) {
            System.err.println("  FAIL: Exception during execution: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
