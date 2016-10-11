package info.puzz.graphanything.utils;

/**
 * Created by puzz on 11/10/16.
 */

public final class StringUtils {
    private StringUtils() {
        throw new Error();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
