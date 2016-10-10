package info.puzz.graphanything.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;

import info.puzz.graphanything.services.ExportImportUtils;
import info.puzz.graphanything.R;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphValue;

public class RawGraphDataActivity extends BaseActivity {

    private static final String TAG = RawGraphDataActivity.class.getSimpleName();

    public static final String ARG_GRAPH_ID = "graph_id";

    private Graph graph;

    EditText editText;
    private long graphId;

    public RawGraphDataActivity() {
        super();
    }

    /**
     * Utility to start this activity from another one.
     */
    public static void start(ActionBarActivity activity, long graphId) {
        Intent intent = new Intent(activity, RawGraphDataActivity.class);
        intent.putExtra(ARG_GRAPH_ID, graphId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_graph_data);
        setTitle(R.string.raw_edit);

        graphId = getIntent().getExtras().getLong(ARG_GRAPH_ID);
    }

    @Override
    protected void onResume() {
        fillData();
        super.onResume();
    }

    private void fillData() {
        graph = getDAO().loadGraph(graphId);
        List<GraphValue> values = getDAO().getValues(graphId);

        editText = (EditText) findViewById(R.id.raw_text);
        try {
            editText.setText(ExportImportUtils.exportGraph(values));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_raw_graph_data, menu);
        return true;
    }

    public void onUpdateRawData(MenuItem item) {
        try {
            List<GraphValue> values = ExportImportUtils.importGraph(graphId, editText.getText().toString());

            getDAO().deleteGraphValues(graphId);

            for (GraphValue value : values) {
                getDAO().addValue(value);
            }

            GraphActivity.start(this, graphId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
