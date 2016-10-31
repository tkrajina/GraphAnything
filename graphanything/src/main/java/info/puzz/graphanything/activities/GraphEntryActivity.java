package info.puzz.graphanything.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.List;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models.FormatVariant;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphColumn;
import info.puzz.graphanything.models.GraphEntry;

public class GraphEntryActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "graph_id";
    private static final String ARG_GRAPH_ENTRY = "graph_entry";

    private LinearLayout columnsLinearLayout;

    private Graph graph;
    private GraphEntry graphEntry;
    private List<GraphColumn> columns;

    public static void start(BaseActivity activity, long graphId, GraphEntry entry) {
        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getGraphId() == graphId);

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

        for (GraphColumn column : columns) {
            View graphColumnView = getLayoutInflater().inflate(R.layout.fragment_graph_edit_column, null);

            TextView columnLabelTextView = (TextView) graphColumnView.findViewById(R.id.column_label);
            EditText columnValueTextView = (EditText) graphColumnView.findViewById(R.id.column_value);

            columnLabelTextView.setText(column.getName() + ":");
            Double value = graphEntry.get(column.getColumnNo());
            columnValueTextView.setText(column.getGraphUnitType().format(value, FormatVariant.LONG));

            columnsLinearLayout.addView(graphColumnView);
        }
    }

    public void onSave(MenuItem item) {
        getDAO().save(graphEntry);
        GraphActivity.start(this, graph._id);
    }
}
