package info.puzz.graphanything.models2;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by puzz on 24.12.14..
 */
@Data
@Accessors(chain = true)
public class Graph implements Serializable {

    public static final int DEFAULT_STATS_SAMPLE_DAYS = 7;

    public Long _id;
    public String name;

    public double lastValue;
    public long lastValueCreated;
    /**
     * Used when this graph timer is active.
     */
    public long timerStarted;
    /**
     * If <code>&gt;0</code> then it's paused.
     * @see #isPaused()
     */
    public long timerPaused;

    /**
     * Used for statistics (calculate current and last sample days) and goal calculation.
     */
    public int statsPeriod = DEFAULT_STATS_SAMPLE_DAYS;

    @Override
    public String toString() {
        return name;
    }

    public boolean isPaused() {
        return timerStarted > 0 && timerPaused > timerStarted;
    }

    public boolean isTimeActive() {
        return timerStarted > 0;
    }

    public static void main(String[] args) {
        System.out.println(String.format("%.2f", 2.3D));
    }
}
