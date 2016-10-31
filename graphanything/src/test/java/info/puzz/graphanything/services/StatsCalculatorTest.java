package info.puzz.graphanything.services;

import com.jjoe64.graphview.series.DataPoint;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.models2.GraphInfo;
import info.puzz.graphanything.models2.GraphEntry;
import info.puzz.graphanything.models2.GraphStats;
import info.puzz.graphanything.models2.GraphType;

/**
 * Created by puzz on 08/10/16.
 */
public class StatsCalculatorTest {

    @Test
    public void testGoal() {
        GraphInfo graph = new GraphInfo();
        graph.setStatsPeriod(7);
        graph.goal = 100D;

        List<GraphEntry> entries = new ArrayList<>();
        entries.add(new GraphEntry().setCreated(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)).set(0, 1D));
        entries.add(new GraphEntry().setCreated(System.currentTimeMillis()).set(0, 2D));

        List<DataPoint> dataPoints = GraphType.VALUES.convert(entries, 0);
        GraphStats stats = StatsCalculator.calculate(graph, dataPoints);

        Assert.assertEquals(98, stats.getGoalEstimateDays().floatValue(), 0.1);
        System.out.println("estimated days=" + stats.getGoalEstimateDays());
    }

}
