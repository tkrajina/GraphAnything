package info.puzz.graphanything.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Utility to log times.
 */
public class Timer {

    private final List<Long> times;
    private final List<String> desc;

    public Timer(String desc) {
        this.times = new ArrayList<>();
        this.desc = new ArrayList<>();
        time(desc);
    }

    public void time(String desc) {
        this.times.add(System.currentTimeMillis());
        this.desc.add(desc);
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.times.size(); i++) {
            long time = i == 0 ? 0 : this.times.get(i) - this.times.get(i - 1);
            res.append(String.format("%8dms ", time)).append(desc.get(i)).append("\n");
        }
        return res.toString();
    }

    public static void main(String[] args) {
        Timer t = new Timer("aaa");
        ThreadUtils.sleep(TimeUnit.SECONDS.toMillis(1));
        t.time("bbb");
        ThreadUtils.sleep(TimeUnit.SECONDS.toMillis(2));
        t.time("ccc");
        System.out.println(t.toString());
    }
}
