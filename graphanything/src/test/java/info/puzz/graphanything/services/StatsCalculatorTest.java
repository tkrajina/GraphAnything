package info.puzz.graphanything.services;

import com.jjoe64.graphview.series.DataPoint;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphStats;
import info.puzz.graphanything.models.GraphType;
import info.puzz.graphanything.models.GraphValue;

/**
 * Created by puzz on 08/10/16.
 */
public class StatsCalculatorTest {

    @Test
    public void testGoal() {
        Graph graph = new Graph();
        graph.setStatsPeriod(7);
        graph.goal = 100D;

        List<GraphValue> values = new ArrayList<>();
        values.add(new GraphValue(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), 1));
        values.add(new GraphValue(System.currentTimeMillis(), 2));

        List<DataPoint> dataPoints = GraphType.VALUES.getConverter().convert(values);
        GraphStats stats = StatsCalculator.calculate(graph, dataPoints);

        Assert.assertEquals(98, stats.getGoalEstimateDays().floatValue(), 0.1);
        System.out.println("estimated days=" + stats.getGoalEstimateDays());
    }

}
