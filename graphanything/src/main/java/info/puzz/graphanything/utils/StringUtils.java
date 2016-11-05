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

    public static String ellipses(String string, int length) {
        if (isEmpty(string)) {
            return string;
        }
        if (string.length() < length) {
            return string;
        }
        return string.substring(0, length) + "…";
    }

    public static final String firstLine(String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        if (string.indexOf("\n") < 0) {
            return string;
        }
        return string.split("\\n")[0] + "…";
    }
}
