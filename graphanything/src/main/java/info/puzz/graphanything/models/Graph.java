package info.puzz.graphanything.models;

import info.puzz.graphanything.utils.TimeUtils;
import lombok.Data;

/**
 * Created by puzz on 24.12.14..
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

    public int unitType = GraphUnitType.UNIT.getType();
    public int type = GraphType.VALUES.getType();

    /**
     * Used for statistics (calculate current and last sample days) and goal calculation.
     */
    public int statsPeriod = DEFAULT_STATS_SAMPLE_DAYS;

    public boolean calculateGoal;
    public double goal;
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

    public boolean isTimeActive() {
        return timerStarted > 0;
    }

    public String formatValueWithUnit(GraphValue value) {
        return formatValueWithUnit(value.value);
    }

    public String formatValueWithUnit(double value) {
        if (GraphUnitType.TIMER.getType() == unitType) {
            return TimeUtils.formatDurationToHHMMSS((long) value);
        } else {
            return String.format("%.2f", value);
        }
    }

    public GraphType getGraphType() {
        for (GraphType graphType : GraphType.values()) {
            if (graphType.getType() == type) {
                return graphType;
            }
        }
        return GraphType.VALUES;
    }

    public static void main(String[] args) {
        System.out.println(String.format("%.2f", 2.3D));
    }
}
