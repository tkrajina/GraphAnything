package info.puzz.graphanything.activities;

import android.content.DialogInterface;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.R;
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

    private LinearLayout columnsLinearLayout;

    private Graph graph;
    private GraphEntry graphEntry;
    private List<GraphColumn> columns;
    private Map<Integer, EditText> columnViewsByColumnNo;
    private EditText commentEditText;
    private EditText createdtEditText;

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
        commentEditText = (EditText) findViewById(R.id.comment);
        createdtEditText = (EditText) findViewById(R.id.created);
        commentEditText.setText(graphEntry.getComment());
        createdtEditText.setText(TimeUtils.YYYYMMDDHHMMSS_FORMATTER.format(new Timestamp(graphEntry.getCreated())));

        initStuff();
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
        return true;
    }

    private void initStuff() {
        Assert.assertNotNull(columnsLinearLayout);
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
                DialogUtils.showWarningDialog(this, "Invalid value:" + valueString, e.getMessage());
                return;
            }
        }

        graphEntry.setComment(commentEditText.getText().toString());
        String createdString = createdtEditText.getText().toString();
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
}
