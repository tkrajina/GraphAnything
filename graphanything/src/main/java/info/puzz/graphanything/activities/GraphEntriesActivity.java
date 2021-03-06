package info.puzz.graphanything.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import info.puzz.graphanything.R;
import info.puzz.graphanything.fragments.GraphEntryFragment;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphEntry;


public class GraphEntriesActivity extends BaseActivity implements GraphEntryFragment.OnFragmentInteractionListener {

    public static final String ARG_GRAPH_ID = "graph_id";

    private long graphId;
    private Graph graph;

    /**
     * Utility to start this activity from another one.
     */
    public static void start(BaseActivity activity, long graphId) {
        Intent intent = new Intent(activity, GraphEntriesActivity.class);
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
            throw new Error(getClass().getSimpleName() + " without graphId!"); // TODO
        }

        graphId = getIntent().getExtras().getLong(ARG_GRAPH_ID);

        setContentView(R.layout.activity_graph_values);

        graph = getDAO().loadGraph(graphId);

        setTitle("\"" + graph.name + "\" values");
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
