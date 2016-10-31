package info.puzz.graphanything.utils;

import java.util.StringJoiner;

/**
 * Created by puzz on 06/10/16.
 */

public class Asserts {

    private Asserts() {
        throw new Error();
    }

    public static void assertTrue(boolean expression) {
        assertTrue(expression, "!");
    }

    private static void assertTrue(boolean expression, String msg) {
        if (!expression) {
            throw new AssertionError(msg);
        }
    }

    public static void assertNotNull(Object expression) {
        assertNotNull(expression, "!");
    }

    public static void assertNotNull(Object expression, String msg) {
        if (expression == null) {
            throw new AssertionError(msg);
        }
    }
}
