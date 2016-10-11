package info.puzz.graphanything.models;

import lombok.Data;

/**
 * @see Graph#statsPeriod
 */
@Data
public class GraphStats {
    private Double avgEntriesDaily;
    private Double avg;
    private Double avgLatestPeriod;
    private Double avgPreviousPeriod;
    private Double sum;
    private Double sumLatestPeriod;
    private Double sumPreviousPeriod;
    private Float goalEstimateDays;
    private long goalTime;
    private GraphValue latestValue;

    // TODO: What if there is no enough data for drawing a line?
    double goalIntercept;
    double goalSlope;

    public double calculateGoalLineValue(double time) {
        return goalIntercept + goalSlope * time;
    }
}
