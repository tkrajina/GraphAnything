package info.puzz.graphanything.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.databinding.ActivityGraphEntryBinding;
import info.puzz.graphanything.models2.FormatVariant;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphEntry;
import info.puzz.graphanything.models2.format.FormatException;
import info.puzz.graphanything.utils.DialogUtils;
import info.puzz.graphanything.utils.TimeUtils;

public class GraphEntryActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "graph_id";
    private static final String ARG_GRAPH_ENTRY = "graph_entry";

    private Graph graph;
    private GraphEntry graphEntry;
    private List<GraphColumn> columns;
    private Map<Integer, EditText> columnViewsByColumnNo;

    ActivityGraphEntryBinding binding;

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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_graph_entry);

        Long graphId = getIntent().getExtras().getLong(ARG_GRAPH_ID);
        graphEntry = (GraphEntry) getIntent().getExtras().getSerializable(ARG_GRAPH_ENTRY);

        Assert.assertNotNull(graphId);
        Assert.assertNotNull(graphEntry);

        graph = getDAO().loadGraph(graphId);
        columns = getDAO().getColumns(graphId);

        Assert.assertNotNull(columns);
        Assert.assertTrue(columns.size() > 0);

        binding.comment.setText(graphEntry.getComment());
        binding.created.setText(TimeUtils.YYYYMMDDHHMMSS_FORMATTER.format(new Timestamp(graphEntry.getCreated())));

        setTitle(graph.name);

        initStuff();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DialogUtils.showYesNoButton(this, "No changes saved, leave?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    GraphEntryActivity.super.onBackPressed();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph_entry, menu);

        menu.findItem(R.id.clone_entry).setVisible(graphEntry._id != null);
        menu.findItem(R.id.action_delete).setVisible(graphEntry._id != null);

        return true;
    }

    private void initStuff() {
        Assert.assertNotNull(columns);
        Assert.assertTrue(columns.size() > 0);

        columnViewsByColumnNo = new HashMap<>();

        for (GraphColumn column : columns) {
            View graphColumnView = getLayoutInflater().inflate(R.layout.fragment_graph_edit_column, null);

            TextView columnLabelTextView = (TextView) graphColumnView.findViewById(R.id.comment_label);
            EditText columnValueTextView = (EditText) graphColumnView.findViewById(R.id.column_value);

            columnLabelTextView.setText(column.formatName() + ":");
            Double value = graphEntry.get(column.getColumnNo());
            columnValueTextView.setText(value == null ? "" : column.getGraphUnitType().format(value, FormatVariant.LONG));

            columnViewsByColumnNo.put(column.getColumnNo(), columnValueTextView);

            binding.columns.addView(graphColumnView);
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
                DialogUtils.showWarningDialog(this, "Invalid value:" + valueString, e.getMessage());
                return;
            }
        }

        graphEntry.setComment(binding.comment.getText().toString());
        String createdString = binding.created.getText().toString();
        try {
            graphEntry.setCreated(TimeUtils.YYYYMMDDHHMMSS_FORMATTER.parse(createdString).getTime());
        } catch (ParseException e) {
            DialogUtils.showWarningDialog(this, "Invalid timestamp:" + createdString, e.getMessage());
            return;
        }

        getDAO().save(graphEntry);

        GraphActivity.start(this, graph._id, 0);
        Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show();
    }

    public void onDelete(MenuItem item) {
        getDAO().delete(graphEntry);
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT);
        GraphActivity.start(this, graph._id, 0);
    }

    public void onNow(View view) {
        binding.created.setText(TimeUtils.YYYYMMDDHHMMSS_FORMATTER.format(new Time(System.currentTimeMillis())));
    }

    public void onClone(MenuItem item) {
        GraphEntry newEntry = new GraphEntry();
        newEntry.setCreated(System.currentTimeMillis());
        newEntry.setGraphId(graph._id);
        newEntry.setComment(graphEntry.getComment());
        for (int columnNo = 0; columnNo < GraphEntry.COLUMNS_NO; columnNo++) {
            newEntry.set(columnNo, graphEntry.get(columnNo));
        }
        GraphEntryActivity.start(this, graph._id, newEntry);
        Toast.makeText(this, "Entry cloned", Toast.LENGTH_LONG).show();
    }
}
