package info.puzz.graphanything.services;

import android.content.SyncStatusObserver;
import android.util.Log;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphStats;
import info.puzz.graphanything.models.GraphValue;

/**
 * Created by puzz on 08/10/16.
 */
public class StatsCalculator {

    private static final String TAG = StatsCalculator.class.getSimpleName();

    private StatsCalculator() {
        throw new Error();
    }

    public static GraphStats calculate(Graph graph, List<GraphValue> valuesOrderedByCreated) {
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

        for (int i = 0; i < valuesOrderedByCreated.size(); i++) {
            GraphValue value = valuesOrderedByCreated.get(i);

            if (res.getLatestValue() == null || value.created > res.getLatestValue().created) {
                res.setLatestValue(value);
            }

            if (i > 0) {
                if (value.created < valuesOrderedByCreated.get(i - 1).created) {
                    throw new Error("Not ordered by created!");
                }
            }

            sum += value.value;
            boolean isThisWeek = value.created > thisWeek;
            boolean isLastWeek = thisWeek > value.created && value.created > lastWeek;
            if (isThisWeek) {
                //Log.d(TAG, new Timestamp(value.created) + " this week");
                sumThisWeek += value.value;
                entriesThisWeek++;
            } else if (isLastWeek) {
                //Log.d(TAG, new Timestamp(value.created) + " last week");
                sumLastWeek += value.value;
                entriesLastWeek++;
            } else {
                //Log.d(TAG, new Timestamp(value.created) + " older");
            }

            if (graph.calculateGoal() && (isThisWeek || isLastWeek)) {
                //System.out.println("data=" + value.created + "," + value.value);
                regression.addData(value.created, value.value);
            }
        }

        res.setSum(sum);
        res.setAvg(sum / valuesOrderedByCreated.size());

        res.setSumLatestPeriod(sumThisWeek);
        res.setAvgLatestPeriod(sumThisWeek / entriesThisWeek);

        res.setSumPreviousPeriod(sumLastWeek);
        res.setAvgPreviousPeriod(sumLastWeek / entriesLastWeek);

        if (graph.calculateGoal()) {
            // y = intercept + slope * x
            res.setGoalIntercept(regression.getIntercept());
            res.setGoalSlope(regression.getSlope());

            //System.out.println(("intercept=" + intercept + " slope=" + slope));

            // TODO: slope == 0;
            long goalTime = (long) ((graph.goal - res.getGoalIntercept()) / res.getGoalSlope());

            //System.out.println("goal time:" + new Timestamp(goalTime));
            //System.out.println("..." + graph.goal);
            //System.out.println("in " + goalTime + "=" + (intercept + slope * goalTime));

            res.setGoalEstimateDays((float) (TimeUnit.MILLISECONDS.toHours(goalTime - System.currentTimeMillis()) / 24.0));
            res.setGoalTime(goalTime);
        }

        return res;
    }

    public static void main(String[] args) {
    }
}
