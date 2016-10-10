package info.puzz.graphanything.utils;

/**
 * Created by puzz on 06/10/16.
 */

public class ThreadUtils {
    private ThreadUtils() {
        throw new Error();
    }
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {}
    }
}
