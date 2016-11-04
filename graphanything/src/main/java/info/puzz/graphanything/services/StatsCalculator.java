package info.puzz.graphanything.services;

import com.jjoe64.graphview.series.DataPoint;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.List;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphStats;
import info.puzz.graphanything.models2.GraphType;

/**
 * Created by puzz on 08/10/16.
 */
public class StatsCalculator {

    private static final String TAG = StatsCalculator.class.getSimpleName();

    private StatsCalculator() {
        throw new Error();
    }

    public static GraphStats calculate(Graph graph, List<DataPoint> dataPoints, GraphColumn column) {
        GraphStats res = new GraphStats();

        int sampleIntervalDays = graph.getStatsPeriod();
        if (sampleIntervalDays <= 0) {
            sampleIntervalDays = Graph.DEFAULT_STATS_SAMPLE_DAYS;
        }

        long thisWeek = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(sampleIntervalDays);
        long lastWeek = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2 * sampleIntervalDays);

        double sum = 0D;

        double sumThisWeek = 0D;
        int entriesThisWeek = 0;

        double sumLastWeek = 0D;
        int entriesLastWeek = 0;

        SimpleRegression regression = new SimpleRegression();

        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint point = dataPoints.get(i);

            double y = point.getY();
            if (column.getGraphType() == GraphType.SUM_ALL_PREVIOUS && i > 0) {
                y -= dataPoints.get(i - 1).getY();
            }

            if (i > 0) {
                if (point.getX() < dataPoints.get(i - 1).getX()) {
                    throw new Error("Not ordered by created!");
                }
            }

            sum += y;
            boolean isThisWeek = point.getX() > thisWeek;
            boolean isLastWeek = thisWeek > point.getX() && point.getX() > lastWeek;
            if (isThisWeek) {
                //Log.d(TAG, new Timestamp(point.created) + " this week");
                sumThisWeek += y;
                entriesThisWeek++;
            } else if (isLastWeek) {
                //Log.d(TAG, new Timestamp(point.created) + " last week");
                sumLastWeek += y;
                entriesLastWeek++;
            } else {
                //Log.d(TAG, new Timestamp(point.created) + " older");
            }

            if (column.calculateGoal() && (isThisWeek || isLastWeek)) {
                regression.addData(point.getX(), point.getY());
            }
        }

        res.setSum(sum);
        res.setAvg(sum / dataPoints.size());

        res.setSumLatestPeriod(sumThisWeek);
        res.setAvgLatestPeriod(sumThisWeek / entriesThisWeek);

        res.setSumPreviousPeriod(sumLastWeek);
        res.setAvgPreviousPeriod(sumLastWeek / entriesLastWeek);

        if (column.calculateGoal() && dataPoints.size() >= 3) {
            // y = intercept + slope * x
            res.setGoalIntercept(regression.getIntercept());
            res.setGoalSlope(regression.getSlope());

            //System.out.println(("intercept=" + intercept + " slope=" + slope));

            if (res.getGoalSlope() != 0) {
                long goalTime = (long) ((column.goal - res.getGoalIntercept()) / res.getGoalSlope());

                res.setGoalEstimateDays((float) (TimeUnit.MILLISECONDS.toHours(goalTime - System.currentTimeMillis()) / 24.0));
                res.setGoalTime(goalTime);
            }
        }

        return res;
    }

}
