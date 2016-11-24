package info.puzz.graphanything.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.databinding.ActivityGraphListBinding;
import info.puzz.graphanything.fragments.GraphListFragment;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.enums.GraphUnitType;


public class GraphListActivity extends BaseActivity implements GraphListFragment.Callbacks {

    ActivityGraphListBinding binding;

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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_graph_list);

        setTitle(R.string.graph_list);

        binding.newGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGraph();
            }
        });
    }

    public void newGraph() {
        CharSequence[] options = new CharSequence[GraphUnitType.values().length];
        for (int i = 0; i < GraphUnitType.values().length; i++) {
            options[i] = GraphUnitType.values()[i].getDescription();
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.graph_type))
                .setNegativeButton(R.string.cancel, null)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGraph(GraphUnitType.values()[which]);
                    }
                })
                .show();
    }

    private void startGraph(GraphUnitType graphUnitType) {
        Graph graph = new Graph();
        GraphColumn column = new GraphColumn();

        column.columnNo = 0;
        column.unitType = graphUnitType.getType();
        if (graphUnitType == GraphUnitType.TIMER) {
            graph.reminderTimerSound = 5;
            graph.finalTimerSound = 30;
            column.name = "Time";
            column.unit = "Time";
        } else {
            column.name = "Value";
            column.unit = "";
        }

        Map<Integer,GraphColumn> columns = new HashMap<>();
        columns.put(0, column);

        GraphEditActivity.start(this, graph, columns);
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
