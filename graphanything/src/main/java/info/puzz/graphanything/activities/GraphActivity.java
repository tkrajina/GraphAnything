package info.puzz.graphanything.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models.FormatVariant;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphStats;
import info.puzz.graphanything.models.GraphUnitType;
import info.puzz.graphanything.models.GraphValue;
import info.puzz.graphanything.models.format.FormatException;
import info.puzz.graphanything.services.StatsCalculator;
import info.puzz.graphanything.utils.AssertUtils;
import info.puzz.graphanything.utils.Formatters;
import info.puzz.graphanything.utils.ThreadUtils;
import info.puzz.graphanything.utils.TimeUtils;
import info.puzz.graphanything.utils.Timer;


public class GraphActivity extends BaseActivity {

    private static final String TAG = GraphActivity.class.getSimpleName();

    public static final String ARG_GRAPH_ID = "graph_id";

    private Graph graph;
    private long graphId;
    private TextView timerTextView;
    private Button startStopTimerButton;

    private boolean activityActive;

    private Float graphFontSize = null;

    /**
     * Utility to start this activity from another one.
     */
    public static void start(ActionBarActivity activity, long graphId) {
        Intent intent = new Intent(activity, GraphActivity.class);
        intent.putExtra(GraphActivity.ARG_GRAPH_ID, graphId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);

        timerTextView = (TextView) findViewById(R.id.timer);
        startStopTimerButton = (Button) findViewById(R.id.start_stop_timer);
        AssertUtils.assertNotNull(timerTextView, "timer");
        AssertUtils.assertNotNull(startStopTimerButton, "timer button");

        if (!getIntent().getExtras().containsKey(ARG_GRAPH_ID)) {
            throw new Error(getClass().getSimpleName() + " without graphId!");
        }

        graphId = getIntent().getExtras().getLong(ARG_GRAPH_ID);

        // Prevent opening the keyboard every time (since the text fiels will have the focus by default):
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityActive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityActive = true;

        graph = getDAO().loadGraph(graphId);

        findViewById(R.id.goal_group).setVisibility(graph.calculateGoal() ? View.VISIBLE : View.GONE);

        setTitle(graph.name);

        boolean isTimer = graph.unitType == GraphUnitType.TIMER.getType();
        findViewById(R.id.timer_value_group).setVisibility(isTimer ? View.VISIBLE : View.GONE);
        findViewById(R.id.unit_value_group).setVisibility(isTimer ? View.GONE : View.VISIBLE);

        if (isTimer) {
            prepareTimer();
        }

        redrawAndUpdateGraphAndStats(true);

        if (graph.isTimeActive()) {
            startTimer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    private void prepareTimer() {
        timerTextView.setText("00:00:00");
        startStopTimerButton.setText(R.string.start_timer);
    }

    public void editGraph(MenuItem item) {
        GraphPropertiesActivity.start(this, graph._id);
    }

    public void showValues(MenuItem item) {
        GraphValuesActivity.start(this, graph._id);
    }

    public void onSaveValue(View view) {
        final EditText numberField = (EditText) findViewById(R.id.number_field);
        String text = numberField.getText().toString().trim();


        double value;
        try {
            value = graph.getGraphUnitType().parse(text);
        } catch (FormatException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid value")
                    .setMessage(e.getMessage())
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            numberField.setText("", TextView.BufferType.EDITABLE);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        addValue(value);

        numberField.setText("");

        // Close keyboard:
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        Toast.makeText(this, "Value added", Toast.LENGTH_SHORT).show();
    }

    private void addValue(double value) {
        getDAO().addValue(graph._id, value);

        graph.lastValue = value;
        graph.lastValueCreated = System.currentTimeMillis();
        graph.timerStarted = 0; // Just in case it's a timer graph
        getDAO().save(graph);

        redrawAndUpdateGraphAndStats(true);
    }

    private void redrawAndUpdateGraphAndStats(boolean showGoal) {
        Timer t = new Timer("redrawing graph");

        GraphView graphView = (GraphView) findViewById(R.id.graph);

        if (graphFontSize == null) {
            graphFontSize = graphView.getGridLabelRenderer().getTextSize();
        }

        graphView.removeAllSeries();

        final GraphUnitType graphUnitType = graph.getGraphUnitType();
        graphView.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            Calendar cal = Calendar.getInstance();
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    cal.setTimeInMillis((long) value);
                    return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                }
                return graphUnitType.format(value, FormatVariant.SHORT);
            }

            @Override
            public void setViewport(Viewport viewport) {

            }
        });

        graphView.getGridLabelRenderer().setTextSize((float) (graphFontSize * 0.6));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            graphView.getGridLabelRenderer().setNumHorizontalLabels(20);
        } else {
            graphView.getGridLabelRenderer().setNumHorizontalLabels(10);
        }

        List<GraphValue> values = getDAO().getValuesByCreatedAsc(graph._id);
        GraphValue latestValue = values.size() == 0 ? null : values.get(values.size() - 1);
        List<DataPoint> dataPoints = graph.getGraphType().convert(values);

        t.time("Before stats");
        GraphStats stats = StatsCalculator.calculate(graph, dataPoints);
        t.time("After stats");

        redrawStats(graphUnitType, stats);

        // And when we already computed all the stats, update the graph entity:
        if (latestValue != null) {
            graph.lastValue = latestValue.value;
            graph.lastValueCreated = latestValue.created;

        }
        if (graph.calculateGoal() && stats.getGoalEstimateDays() != null) {
            graph.goalEstimateDays = stats.getGoalEstimateDays();
        }
        getDAO().save(graph);

        t.time("before getting graph points");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
        t.time("after getting graph points");

        double minX = series.getLowestValueX();
        double maxX = series.getHighestValueX();
        double minY = series.getLowestValueY();
        double maxY = series.getHighestValueY();

        if (graph.calculateGoal() && showGoal) {
            minY = Math.min(minY, graph.goal);
            maxY = Math.max(maxY, graph.goal);

            long goalTime = stats.getGoalTime();

            long maxGoalDistance = (long) ((maxX - minX) * 2);
            long fromGoalTime = stats.getGoalTime() - System.currentTimeMillis();
            if (fromGoalTime > maxGoalDistance) {
                goalTime = (long) (System.currentTimeMillis() + maxGoalDistance * 0.75);
            } else if (fromGoalTime < - maxGoalDistance) {
                goalTime = (long) (System.currentTimeMillis() - maxGoalDistance * 0.75);
            }

            minX = Math.min(minX, goalTime);
            maxX = Math.max(maxX, goalTime);

            // Goal line:
            LineGraphSeries<DataPoint> goalSeries = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(minX, graph.goal),
                    new DataPoint(maxX, graph.goal),
            });
            goalSeries.setThickness(2);
            goalSeries.setColor(0xffff9c00);
            goalSeries.setTitle(getResources().getString(R.string.goal));
            graphView.addSeries(goalSeries);

            // Graph estimate line:
            LineGraphSeries<DataPoint> estimateSeries = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(minX, stats.calculateGoalLineValue(minX)),
                    new DataPoint(maxX, stats.calculateGoalLineValue(maxX)),
            });
            estimateSeries.setThickness(2);
            estimateSeries.setTitle(getResources().getString(R.string.estimate));
            estimateSeries.setColor(0xff67cfff);
            graphView.addSeries(estimateSeries);
        }

        if (minX == maxX) {
            minX = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);
            maxX = System.currentTimeMillis();
            minY = 0;
            maxY = 10;
        }

        double xpadding = minX == maxX ? 0 : (maxX - minX) * 0.01;
        double ypadding = minY == maxY ? 0 : (maxY - minY) * 0.01;
        minX -= xpadding;
        maxX += xpadding;
        minY -= ypadding;
        maxY += ypadding;

        graphView.getViewport().setMinX(minX);
        graphView.getViewport().setMaxX(maxX);
        graphView.getViewport().setXAxisBoundsManual(true);

        graphView.getViewport().setMinY(minY);
        graphView.getViewport().setMaxY(maxY);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setGridColor(Color.GRAY);

        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphView.getLegendRenderer().setVisible(true);

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);

        series.setTitle(getResources().getString(R.string.data));

        if (dataPoints.size() < 6) {
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(graphFontSize / dataPoints.size());
        }

        t.time("Before drawing graph");
        graphView.addSeries(series);
        t.time("After drawing graph");

        Log.i(TAG, "Graph drawing times:" + t.toString());
    }

    private void redrawStats(GraphUnitType graphUnitType, GraphStats stats) {
        ((TextView) findViewById(R.id.total_avg)).setText(graphUnitType.format(stats.getAvg(), FormatVariant.LONG));
        ((TextView) findViewById(R.id.last_preriod_avg_value)).setText(graphUnitType.format(stats.getAvgLatestPeriod(), FormatVariant.SHORT));
        ((TextView) findViewById(R.id.previous_preriod_avg_value)).setText(graphUnitType.format(stats.getAvgPreviousPeriod(), FormatVariant.SHORT));

        ((TextView) findViewById(R.id.total_sum)).setText(graphUnitType.format(stats.getSum(), FormatVariant.LONG));
        ((TextView) findViewById(R.id.last_preriod_sum_value)).setText(graphUnitType.format(stats.getSumLatestPeriod(), FormatVariant.SHORT));
        ((TextView) findViewById(R.id.previous_period_sum_value)).setText(graphUnitType.format(stats.getSumPreviousPeriod(), FormatVariant.SHORT));

        if (graph.calculateGoal()) {
            ((TextView) findViewById(R.id.goal)).setText(graphUnitType.format(graph.goal, FormatVariant.LONG));
            ((TextView) findViewById(R.id.goal_estimate)).setText(stats.getGoalEstimateDays() == null ? "n/a" : Formatters.formatNumber(stats.getGoalEstimateDays()) + "days");
        }
    }

    public void onEditRawData(MenuItem item) {
        RawGraphDataActivity.start(this, graph._id);
    }

    public void deleteGraph(MenuItem item) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    deleteGraph();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Delete \"" + graph.name + "\"?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    private void deleteGraph() {
        getDAO().deleteGraph(graph);

        GraphListActivity.start(this);
    }

    public void onStartStop(View view) {
        if (graph.unitType != GraphUnitType.TIMER.getType()) {
            return;
        }
        if (graph.isTimeActive()) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void stopTimer() {
        long value = TimeUtils.timeFrom(graph.timerStarted);
        addValue(value);
        startStopTimerButton.setText(R.string.start_timer);
    }

    /**
     * Starts a thread to update the timer. The thread will automatically stop when the activity pauses or the {@link Graph#timerStarted} is set to 0.
     */
    private void startTimer() {
        if (graph.timerStarted == 0) {
            graph.timerStarted = System.currentTimeMillis();
            getDAO().save(graph);
        }
        startStopTimerButton.setText(R.string.stop_timer);

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Starting timer update thread");
                while (activityActive && graph.isTimeActive()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerTextView.setText(TimeUtils.formatDurationToHHMMSS(System.currentTimeMillis() - graph.timerStarted, FormatVariant.LONG));
                        }
                    });

                    ThreadUtils.sleep(TimeUnit.SECONDS.toMillis(1));
                }
                Log.i(TAG, "Timer stopped");
            }
        }.start();
    }
}
