package info.puzz.graphanything.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.databinding.ActivityGraphEditBinding;
import info.puzz.graphanything.models.format.FormatException;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.enums.GraphUnitType;
import info.puzz.graphanything.utils.DialogUtils;
import info.puzz.graphanything.utils.ParserUtils;


public class GraphEditActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "graph_id";
    private static final String ARG_GRAPH = "graph";
    private static final String ARG_GRAPH_COLUMNS = "graph_columns";

    ActivityGraphEditBinding binding;

    private Map<Integer, GraphColumn> columnsByColumnNumbers;

    public static void start(BaseActivity activity, Long graphId) {
        Intent intent = new Intent(activity, GraphEditActivity.class);
        intent.putExtra(GraphEditActivity.ARG_GRAPH_ID, graphId);
        activity.startActivity(intent);
    }

    public static void start(BaseActivity activity, Graph graph, Map<Integer, GraphColumn> columns) {
        Assert.assertNotNull(graph);
        Assert.assertNotNull(columns);

        Intent intent = new Intent(activity, GraphEditActivity.class);
        intent.putExtra(ARG_GRAPH, graph);
        intent.putExtra(ARG_GRAPH_COLUMNS, (Serializable) columns);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disallowWalkAway(R.string.no_changes_saved_message);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_graph_edit);

        Graph graph = (Graph) getIntent().getSerializableExtra(ARG_GRAPH);
        if (graph == null) {
            Long graphId = (Long) getIntent().getExtras().get(ARG_GRAPH_ID);
            Assert.assertNotNull(graphId);
            graph = getDAO().loadGraph(graphId);
            columnsByColumnNumbers = getDAO().getColumnsByColumnNo(graphId);
            setTitle(R.string.action_edit);
        } else {
            columnsByColumnNumbers = (Map<Integer, GraphColumn>) getIntent().getExtras().getSerializable(ARG_GRAPH_COLUMNS);
            Assert.assertNotNull(columnsByColumnNumbers);
            Assert.assertTrue(columnsByColumnNumbers.containsKey(0));
        }
        binding.setGraph(graph);
        binding.setFirstColumn(columnsByColumnNumbers.get(0));

        setTitle(R.string.edit_graph);

        GraphColumn firstColumn = columnsByColumnNumbers.get(0);
        Assert.assertNotNull(firstColumn);

        binding.timerSoundsGroup.setVisibility(firstColumn.getGraphUnitType() == GraphUnitType.TIMER ? View.VISIBLE : View.GONE);

        reloadFields();
    }

    private void reloadFields() {
        Integer freeColumnNo = null;
        final List<GraphColumn> columns = new ArrayList<>(columnsByColumnNumbers.size());
        for (int columnNo = 0; columnNo < GraphColumn.COLUMNS_NO; columnNo++) {
            if (columnsByColumnNumbers.containsKey(columnNo)) {
                columns.add(columnsByColumnNumbers.get(columnNo));
            } else if (freeColumnNo == null) {
                freeColumnNo = columnNo;
            }
        }

        if (freeColumnNo != null) {
            columns.add(null);
        }

        final Integer freeColumnNoFinal = freeColumnNo;

        for (final GraphColumn graphColumn : columns) {
            View graphColumnView = getLayoutInflater().inflate(R.layout.fragment_graph_column_info, null);

            Button editGraphButton = (Button) graphColumnView.findViewById(R.id.edit_column);

            if (graphColumn == null) {
                Assert.assertNotNull(freeColumnNoFinal);
                editGraphButton.setText(R.string.add_new_column);
                editGraphButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        columnsByColumnNumbers.put(freeColumnNoFinal, new GraphColumn().setColumnNo(freeColumnNoFinal));
                        try {
                            GraphColumnActivity.start(GraphEditActivity.this, getFormGraph(), columnsByColumnNumbers, freeColumnNoFinal);
                        } catch (FormatException e) {
                            DialogUtils.showWarningDialog(GraphEditActivity.this, "Error", e.getMessage());
                        }
                    }
                });
            } else {
                editGraphButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            GraphColumnActivity.start(GraphEditActivity.this, getFormGraph(), columnsByColumnNumbers, graphColumn.getColumnNo());
                        } catch (FormatException e) {
                            DialogUtils.showWarningDialog(GraphEditActivity.this, "Error", e.getMessage());
                        }
                    }
                });

                TextView graphColumnTextView = (TextView) graphColumnView.findViewById(R.id.graph_column_description);
                graphColumnTextView.setText(graphColumn.formatName());
                editGraphButton.setText(R.string.change);
                //editGraphButton.setText(R.string.enable);
            }

            binding.fields.addView(graphColumnView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph_properties, menu);
        return true;
    }

    public void onSaveGraph(MenuItem item) {
        Graph graph = null;
        try {
            graph = getFormGraph();
        } catch (FormatException e) {
            DialogUtils.showWarningDialog(this, "Invalid field", e.getMessage());
            return;
        }

        if (!columnsByColumnNumbers.containsKey(0)) {
            DialogUtils.showWarningDialog(this, "At least one field must be defined", "Please add one numeric field for the graph");
            return;
        }

        getDAO().save(graph);

        Map<Integer, GraphColumn> currentColumns = getDAO().getColumnsByColumnNo(graph._id);
        for (Map.Entry<Integer, GraphColumn> e : currentColumns.entrySet()) {
            if (!columnsByColumnNumbers.containsKey(e.getKey())) {
                getDAO().delete(e.getValue());
            }
        }
        for (Map.Entry<Integer, GraphColumn> e : columnsByColumnNumbers.entrySet()) {
            e.getValue().setGraphId(graph._id);
            getDAO().save(e.getValue());
        }

        allowWalkAway();
        GraphActivity.start(this, graph._id, 0);

        Toast.makeText(this, "Graph saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Graph updated with form values.
     */
    private Graph getFormGraph() throws FormatException {
        Graph graph = binding.getGraph();
        graph.name = binding.graphName.getText().toString();
        graph.setReminderTimerSound(ParserUtils.parseInteger(binding.reminderSound.getText().toString(), 0));
        graph.setFinalTimerSound(ParserUtils.parseInteger(binding.finalSound.getText().toString(), 0));
        return graph;
    }

}