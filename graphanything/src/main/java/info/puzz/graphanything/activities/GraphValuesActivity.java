package info.puzz.graphanything.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import info.puzz.graphanything.R;
import info.puzz.graphanything.fragments.GraphValueFragment;
import info.puzz.graphanything.models2.GraphInfo;
import info.puzz.graphanything.models2.GraphEntry;


public class GraphValuesActivity extends BaseActivity implements GraphValueFragment.OnFragmentInteractionListener {

    public static final String ARG_GRAPH_ID = "graph_id";

    private long graphId;
    private GraphInfo graph;

    /**
     * Utility to start this activity from another one.
     */
    public static void start(BaseActivity activity, long graphId) {
        Intent intent = new Intent(activity, GraphValuesActivity.class);
        intent.putExtra(GraphActivity.ARG_GRAPH_ID, graphId);
        activity.startActivity(intent);
    }

    public long getGraphId() {
        return graphId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null || !getIntent().getExtras().containsKey(ARG_GRAPH_ID)) {
            throw new Error(getClass().getSimpleName() + " withour graphId!");
        }

        graphId = getIntent().getExtras().getLong(ARG_GRAPH_ID);

        setContentView(R.layout.activity_graph_values);

        graph = getDAO().loadGraph(graphId);

        setTitle("\"" + graph.name + "\" values");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph_values, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(GraphEntry graphEntry) {
        GraphEntryActivity.start(this, graph._id, graphEntry);
    }
}
