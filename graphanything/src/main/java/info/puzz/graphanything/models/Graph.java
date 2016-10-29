package info.puzz.graphanything.models;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;

/**
 * Created by puzz on 24.12.14..
 */
@Data
public class Graph implements Serializable {

    public static final int DEFAULT_STATS_SAMPLE_DAYS = 7;

    public Long _id;
    public String name;

    /** @deprecated See {@link GraphColumn} */
    public String unit;

    public double lastValue;
    public long lastValueCreated;
    /**
     * Used when this graph timer is active.
     */
    public long timerStarted;

    public int type = GraphType.VALUES.getType();

    /** @deprecated See {@link GraphColumn} */
    public int unitType = GraphUnitType.UNIT.getType();

    /**
     * Used for statistics (calculate current and last sample days) and goal calculation.
     */
    public int statsPeriod = DEFAULT_STATS_SAMPLE_DAYS;

    /** @deprecated See {@link GraphColumn} */
    public Double goal;

    /**
     * Number of days to reach goal. It is calculated by comparing the average in the current
     * and previous interval ({@link #statsPeriod} days).
     *
     * If it's negative then the value is diverging from the goal.
     *
     * @deprecated See {@link GraphColumn}
     */
    public float goalEstimateDays;

    @Override
    public String toString() {
        return name;
    }

    @Deprecated
    public boolean calculateGoal() {
        return goal != null;
    }

    public boolean isTimeActive() {
        return timerStarted > 0;
    }

    public String formatValueWithUnit(double value, FormatVariant variant) {
        return getGraphUnitType().format(value, variant);
    }

    public GraphUnitType getGraphUnitType() {
        for (GraphUnitType t : GraphUnitType.values()) {
            if (t.getType() == unitType) {
                return t;
            }
        }
        return GraphUnitType.UNIT;
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
