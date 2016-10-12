package info.puzz.graphanything.models;

import info.puzz.graphanything.models.format.FormatException;
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

    public boolean calculateGoal() {
        return goal != null;
    }

    public boolean isTimeActive() {
        return timerStarted > 0;
    }

    public String formatValueWithUnit(GraphValue value, FormatVariant variant) {
        return formatValueWithUnit(value.value, variant);
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
