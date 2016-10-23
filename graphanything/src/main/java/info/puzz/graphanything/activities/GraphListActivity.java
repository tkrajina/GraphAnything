package info.puzz.graphanything.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import info.puzz.graphanything.R;
import info.puzz.graphanything.fragments.GraphListFragment;


public class GraphListActivity extends BaseActivity implements GraphListFragment.Callbacks {

    /**
     * Utility to start this activity from another one.
     */
    public static void start(BaseActivity activity) {
        Intent intent = new Intent(activity, GraphListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph_list);

        setTitle(R.string.graph_list);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void newGraph(MenuItem item) {
        GraphPropertiesActivity.start(this, null);
    }

    /**
     * Callback method from {@link GraphListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onGraphSelected(long id) {
        Intent detailIntent = new Intent(this, GraphActivity.class);
        detailIntent.putExtra(GraphActivity.ARG_GRAPH_ID, id);
        startActivity(detailIntent);
    }

    public void showInfo(MenuItem item) {
        AboutActivity.start(this);
    }
}
