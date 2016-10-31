package info.puzz.graphanything.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models.FormatVariant;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphColumn;
import info.puzz.graphanything.models.GraphEntry;
import info.puzz.graphanything.models.format.FormatException;

public class GraphEntryActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "graph_id";
    private static final String ARG_GRAPH_ENTRY = "graph_entry";

    private LinearLayout columnsLinearLayout;

    private Graph graph;
    private GraphEntry graphEntry;
    private List<GraphColumn> columns;
    private Map<Integer, EditText> columnViewsByColumnNo;

    public static void start(BaseActivity activity, long graphId, GraphEntry entry) {
        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getGraphId() == graphId);
        Assert.assertTrue(entry.getCreated() > 0);

        Intent intent = new Intent(activity, GraphEntryActivity.class);
        intent.putExtra(ARG_GRAPH_ID, graphId);
        intent.putExtra(ARG_GRAPH_ENTRY, entry);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_entry);

        Long graphId = getIntent().getExtras().getLong(ARG_GRAPH_ID);
        graphEntry = (GraphEntry) getIntent().getExtras().getSerializable(ARG_GRAPH_ENTRY);
        Assert.assertNotNull(graphId);
        Assert.assertNotNull(graphEntry);

        graph = getDAO().loadGraph(graphId);
        columns = getDAO().getColumns(graphId);

        Assert.assertNotNull(columns);
        Assert.assertTrue(columns.size() > 0);

        columnsLinearLayout = (LinearLayout) findViewById(R.id.columns);

        initStuff();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph_entry, menu);
        return true;
    }

    private void initStuff() {
        Assert.assertNotNull(columnsLinearLayout);
        Assert.assertNotNull(columns);
        Assert.assertTrue(columns.size() > 0);

        columnViewsByColumnNo = new HashMap<>();

        for (GraphColumn column : columns) {
            View graphColumnView = getLayoutInflater().inflate(R.layout.fragment_graph_edit_column, null);

            TextView columnLabelTextView = (TextView) graphColumnView.findViewById(R.id.column_label);
            EditText columnValueTextView = (EditText) graphColumnView.findViewById(R.id.column_value);

            columnLabelTextView.setText(column.getName() + ":");
            Double value = graphEntry.get(column.getColumnNo());
            columnValueTextView.setText(value == null ? "" : column.getGraphUnitType().format(value, FormatVariant.LONG));

            columnViewsByColumnNo.put(column.getColumnNo(), columnValueTextView);

            columnsLinearLayout.addView(graphColumnView);
        }
    }

    public void onSave(MenuItem item) {
        for (GraphColumn column : columns) {
            EditText columnValueTextView = columnViewsByColumnNo.get(column.getColumnNo());
            Assert.assertNotNull("columnNo=" + column.getColumnNo(), columnValueTextView);

            String valueString = columnValueTextView.getText().toString();
            try {
                graphEntry.set(column.columnNo, column.getGraphUnitType().parse(valueString));
            } catch (FormatException e) {
                new AlertDialog.Builder(this)
                        .setTitle("Invalid value:" + valueString)
                        .setMessage(e.getMessage())
                        .setNeutralButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return;
            }
        }

        getDAO().save(graphEntry);

        Toast.makeText(this, "Value added", Toast.LENGTH_SHORT).show();
        GraphActivity.start(this, graph._id);
    }
}