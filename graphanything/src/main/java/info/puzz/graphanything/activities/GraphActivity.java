package info.puzz.graphanything.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import junit.framework.Assert;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models2.FormatVariant;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphEntry;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphStats;
import info.puzz.graphanything.models2.GraphUnitType;
import info.puzz.graphanything.models2.format.FormatException;
import info.puzz.graphanything.services.StatsCalculator;
import info.puzz.graphanything.utils.DialogUtils;
import info.puzz.graphanything.utils.Formatters;
import info.puzz.graphanything.utils.StringUtils;
import info.puzz.graphanything.utils.ThreadUtils;
import info.puzz.graphanything.utils.TimeUtils;
import info.puzz.graphanything.utils.Timer;


public class GraphActivity extends BaseActivity {

    private static final String TAG = GraphActivity.class.getSimpleName();

    public static final String ARG_GRAPH_ID = "graph_id";
    public static final String ARG_GRAPH = "graph";
    public static final String ARG_COLUMN_NO = "column_no";

    private Long graphId;
    private Graph graph;
    private List<GraphColumn> graphColumns;
    private GraphColumn currentGraphColumn;
    private boolean activityActive;
    private Float graphFontSize = null;

    private TextView timerTextView;
    private Button startStopTimerButton;
    private View fieldSelectorGroup;
    private Spinner fieldSpinner;
    private TextView goalTextView;
    private TextView goalEstimateTextView;
    private View goalGroup;
    private List<GraphEntry> values;
    private TextView valueTextView;
    private Button pauseResumeButton;

    public static void start(BaseActivity activity, long graphId, int columnNo) {
        Intent intent = new Intent(activity, GraphActivity.class);
        intent.putExtra(GraphActivity.ARG_GRAPH_ID, graphId);
        intent.putExtra(GraphActivity.ARG_COLUMN_NO, columnNo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);

        Assert.assertNotNull(timerTextView = (TextView) findViewById(R.id.timer));
        Assert.assertNotNull(startStopTimerButton = (Button) findViewById(R.id.start_stop_timer));
        Assert.assertNotNull(fieldSelectorGroup = findViewById(R.id.field_selector_group));
        Assert.assertNotNull(fieldSpinner = (Spinner) findViewById(R.id.field_selector));
        Assert.assertNotNull(goalTextView = (TextView) findViewById(R.id.goal));
        Assert.assertNotNull(goalEstimateTextView = (TextView) findViewById(R.id.goal_estimate));
        Assert.assertNotNull(goalGroup = findViewById(R.id.goal_group));
        Assert.assertNotNull(valueTextView = (TextView) findViewById(R.id.value));
        Assert.assertNotNull(pauseResumeButton = (Button) findViewById(R.id.pause_resume_timer));

        Assert.assertNotNull(graphId = getIntent().getExtras().getLong(ARG_GRAPH_ID));
        graphColumns = getDAO().getColumns(graphId);
        int columnNo = getIntent().getExtras().getInt(ARG_COLUMN_NO);
        for (GraphColumn graphColumn : graphColumns) {
            if (graphColumn.getColumnNo() == columnNo) {
                currentGraphColumn = graphColumn;
            }
        }
        Assert.assertNotNull(String.format("columnNo=%d, currentGraphColumn=%s", columnNo, currentGraphColumn), currentGraphColumn);

        graph = getDAO().loadGraph(graphId);

        prepareFieldSpinner();

        // Prevent opening the keyboard every time (since the text fiels will have the focus by default):
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void prepareFieldSpinner() {
        if (graphColumns.size() <= 1) {
            return;
        }

        String[] fieldsArr = new String[graphColumns.size()];
        for (int i = 0; i < graphColumns.size(); i++) {
            fieldsArr[i] = graphColumns.get(i).formatName();
        }

        fieldSelectorGroup.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fieldsArr);
        fieldSpinner.setAdapter(adapter);

        fieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                GraphColumn selectedColumn = graphColumns.get(position);
                if (selectedColumn.getColumnNo() != currentGraphColumn.getColumnNo()) {
                    Log.i(TAG, "Selected column:" + selectedColumn);
                    currentGraphColumn = selectedColumn;
                    redrawAll();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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

        setTitle(graph.name);

        redrawAll();
    }

    private void redrawAll() {
        boolean isTimer = currentGraphColumn.getGraphUnitType() == GraphUnitType.TIMER;
        findViewById(R.id.timer_value_group).setVisibility(isTimer ? View.VISIBLE : View.GONE);
        findViewById(R.id.unit_value_group).setVisibility(isTimer ? View.GONE : View.VISIBLE);

        if (isTimer) {
            restartTimerElements();
        } else {
            valueTextView.setText(StringUtils.ellipses(graphColumns.get(0).getName() + ":", 15));
        }

        redrawAndUpdateGraphAndStats();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        menu.findItem(R.id.action_edit_latest_entry).setVisible(values.size() > 0);
        return true;
    }

    public void onEditGraph(MenuItem item) {
        GraphEditActivity.start(this, graph._id);
    }

    public void onShowValues(MenuItem item) {
        GraphEntriesActivity.start(this, graph._id);
    }

    public void onSaveValue(View view) {
        final EditText numberField = (EditText) findViewById(R.id.number_field);
        String text = numberField.getText().toString().trim();


        double value;
        try {
            value = currentGraphColumn.getGraphUnitType().parse(text);
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

        this.addValue(value);
    }

    private void addValue(double value) {
        GraphEntry graphEntry = new GraphEntry()
                .setGraphId(graph._id)
                .setCreated(System.currentTimeMillis())
                .set(0, value);
        GraphEntryActivity.start(this, graph._id, graphEntry);
        getDAO().save(graph.setTimerStarted(0));
    }

    private void redrawAndUpdateGraphAndStats() {
        Timer t = new Timer("redrawing graph");

        GraphView graphView = (GraphView) findViewById(R.id.graph);

        if (graphFontSize == null) {
            graphFontSize = graphView.getGridLabelRenderer().getTextSize();
        }

        graphView.removeAllSeries();

        final GraphUnitType graphUnitType = currentGraphColumn.getGraphUnitType();
        graphView.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            public String lattestFormatted;
            Calendar cal = Calendar.getInstance();
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    cal.setTimeInMillis((long) value);
                    String formatted = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                    if (formatted.equals(lattestFormatted)) {
                        this.lattestFormatted = formatted;
                        return "";
                    }
                    this.lattestFormatted = formatted;
                    return formatted;
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

        values = getDAO().getEntriesByCreatedAsc(graph._id);

        GraphEntry latestValue = values.size() == 0 ? null : values.get(values.size() - 1);
        List<DataPoint> dataPoints = currentGraphColumn.getGraphType().convert(values, currentGraphColumn.getColumnNo());

        t.time("Before stats");
        GraphStats stats = StatsCalculator.calculate(graph, dataPoints, currentGraphColumn);
        t.time("After stats");

        redrawStats(graphUnitType, stats);

        // And when we already computed all the stats, update the graph entity:
        if (latestValue != null) {
            graph.lastValue = latestValue.get(0);
            graph.lastValueCreated = latestValue.created;

        }
        if (currentGraphColumn.calculateGoal() && stats.getGoalEstimateDays() != null) {
            currentGraphColumn.goalEstimateDays = stats.getGoalEstimateDays();
        }
        getDAO().save(graph);
        getDAO().save(currentGraphColumn);

        t.time("before getting graph points");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
        t.time("after getting graph points");

        double minX = series.getLowestValueX();
        double maxX = series.getHighestValueX();
        double minY = series.getLowestValueY();
        double maxY = series.getHighestValueY();

        if (currentGraphColumn.calculateGoal()) {
            minY = Math.min(minY, currentGraphColumn.goal);
            maxY = Math.max(maxY, currentGraphColumn.goal);

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
                    new DataPoint(minX, currentGraphColumn.goal),
                    new DataPoint(maxX, currentGraphColumn.goal),
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
            minX -= 10;
            maxX += 10;
            minY -= 10;
            maxY += 10;
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

        LegendRenderer.LegendAlign legendAlign = LegendRenderer.LegendAlign.BOTTOM;
        if (dataPoints.size() > 1 && dataPoints.get(0).getY() > dataPoints.get(dataPoints.size() - 1).getY()) {
            legendAlign = LegendRenderer.LegendAlign.TOP;
        }
        graphView.getLegendRenderer().setAlign(legendAlign);
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
        boolean horizontal = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        ((TextView) findViewById(R.id.total_avg)).setText(graphUnitType.format(stats.getAvg(), FormatVariant.LONG));
        ((TextView) findViewById(R.id.last_preriod_avg_value)).setText(graphUnitType.format(stats.getAvgLatestPeriod(), horizontal ? FormatVariant.LONG : FormatVariant.SHORT));
        ((TextView) findViewById(R.id.previous_preriod_avg_value)).setText(graphUnitType.format(stats.getAvgPreviousPeriod(), horizontal ? FormatVariant.LONG : FormatVariant.SHORT));

        ((TextView) findViewById(R.id.total_sum)).setText(graphUnitType.format(stats.getSum(), FormatVariant.LONG));
        ((TextView) findViewById(R.id.last_preriod_sum_value)).setText(graphUnitType.format(stats.getSumLatestPeriod(), horizontal ? FormatVariant.LONG : FormatVariant.SHORT));
        ((TextView) findViewById(R.id.previous_period_sum_value)).setText(graphUnitType.format(stats.getSumPreviousPeriod(), horizontal ? FormatVariant.LONG : FormatVariant.SHORT));

        if (currentGraphColumn.calculateGoal()) {
            goalGroup.setVisibility(View.VISIBLE);
            goalTextView.setText(graphUnitType.format(currentGraphColumn.goal, FormatVariant.LONG));

            String estimate = "n/a";
            if (stats.getGoalEstimateDays() != null && stats.getGoalEstimateDays().floatValue() >= 0) {
                estimate = Formatters.formatNumber(stats.getGoalEstimateDays()) + "days";
            }
            goalEstimateTextView.setText(estimate);
        } else {
            goalGroup.setVisibility(View.GONE);
        }
    }

/*    public void onEditRawData(MenuItem item) {
        RawGraphDataActivity.start(this, graph._id);
    }*/

    public void onDeleteGraph(MenuItem item) {
        DialogUtils.showYesNoButton(this, "Delete \"" + graph.name + "\"?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    onDeleteGraph();
                }
            }
        });
    }

    private void onDeleteGraph() {
        getDAO().delete(graph);

        GraphListActivity.start(this);
        Toast.makeText(this, "Graph deleted", Toast.LENGTH_SHORT);
    }

    public void onStartStop(View view) {
        if (currentGraphColumn.unitType != GraphUnitType.TIMER.getType()) {
            return;
        }
        if (graph.isTimerActive()) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    public void onPauseResume(View view) {
        if (currentGraphColumn.unitType != GraphUnitType.TIMER.getType()) {
            return;
        }
        if (graph.isPaused()) {
            resumeTimer();
        } else {
            pauseTimer();
        }
    }

    public void onEditLatestEntry(MenuItem item) {
        GraphEntry entry = getDAO().getLatestEntry(graph._id);
        GraphEntryActivity.start(this, graph._id, entry);
    }

    private void stopTimer() {
        long value = TimeUtils.timeFrom(graph.timerStarted);
        startStopTimerButton.setText(R.string.start_timer);
        addValue(value);
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
        pauseResumeButton.setVisibility(View.VISIBLE);

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Starting timer update thread");
                while (activityActive && graph.isTimerActive() && !graph.isPaused()) {
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

    private void pauseTimer() {
        graph.setTimerPaused(System.currentTimeMillis());
        getDAO().save(graph);
        restartTimerElements();
        Assert.assertTrue(graph.isPaused());
        Assert.assertTrue(graph.isTimerActive());
    }

    private void resumeTimer() {
        long timeRunning = graph.getTimerPaused() - graph.getTimerStarted();
        graph.setTimerStarted(System.currentTimeMillis() - timeRunning);
        graph.setTimerPaused(0);
        getDAO().save(graph);
        restartTimerElements();
        Assert.assertFalse(graph.isPaused());
        Assert.assertTrue(graph.isTimerActive());
    }

    private void restartTimerElements() {
        if (graph.isTimerActive()) {
            if (graph.isPaused()) {
                long time = graph.getTimerPaused() - graph.getTimerStarted();
                timerTextView.setText(GraphUnitType.TIMER.format((double) time, FormatVariant.LONG));

                pauseResumeButton.setVisibility(View.VISIBLE);
                pauseResumeButton.setText(R.string.resume);
                startStopTimerButton.setVisibility(View.GONE);
            } else {
                long time = System.currentTimeMillis() - graph.getTimerStarted();
                timerTextView.setText(GraphUnitType.TIMER.format((double) time, FormatVariant.LONG));

                pauseResumeButton.setVisibility(View.VISIBLE);
                pauseResumeButton.setText(R.string.pause);
                startStopTimerButton.setVisibility(View.VISIBLE);
                startStopTimerButton.setText(R.string.stop_timer);
                startTimer();
            }
        } else {
            pauseResumeButton.setVisibility(View.GONE);
            startStopTimerButton.setVisibility(View.VISIBLE);
            startStopTimerButton.setText(R.string.start_timer);
        }
    }

}
