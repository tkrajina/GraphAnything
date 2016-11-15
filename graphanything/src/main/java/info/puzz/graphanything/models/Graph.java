package info.puzz.graphanything.models;

import info.puzz.graphanything.models2.enums.GraphType;
import info.puzz.graphanything.models2.enums.GraphUnitType;
import lombok.Data;

/**
 * @deprecated
 * @see info.puzz.graphanything.models2.Graph
 */
@Data
public class Graph {

    public static final int DEFAULT_STATS_SAMPLE_DAYS = 7;

    public Long _id;
    public String name;
    public String unit;

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

    public int unitType = GraphUnitType.UNIT.getType();
    public int type = GraphType.VALUES.getType();

    /**
     * Used for statistics (calculate current and last sample days) and goal calculation.
     */
    public int statsPeriod = DEFAULT_STATS_SAMPLE_DAYS;

    public Double goal;
    /**
     * Number of days to reach goal. It is calculated by comparing the average in the current
     * and previous interval ({@link #statsPeriod} days).
     *
     * If it's negative then the value is diverging from the goal.
     */
    public float goalEstimateDays;

    @Override
    public String toString() {
        return name;
    }

    public boolean isPaused() {
        return timerStarted > 0 && timerPaused > timerStarted;
    }

    public static void main(String[] args) {
        System.out.println(String.format("%.2f", 2.3D));
    }
}
