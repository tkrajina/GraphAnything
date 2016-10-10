package info.puzz.graphanything.utils;

/**
 * Created by puzz on 06/10/16.
 */

public class AssertUtils {
    private AssertUtils() {
        throw new Error();
    }
    public static void assertNotNull(Object expression, String msg) {
        if (expression == null) {
            throw new AssertionError(msg);
        }
    }
}
