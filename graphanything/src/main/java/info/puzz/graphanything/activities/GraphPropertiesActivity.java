package info.puzz.graphanything.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphColumn;
import info.puzz.graphanything.models.GraphType;


public class GraphPropertiesActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "graph_id";

    private EditText graphNameEditText;
    private ListView fieldsListView;

    private Graph graph;

    public static void start(BaseActivity activity, Long graphId) {
        Intent intent = new Intent(activity, GraphPropertiesActivity.class);
        intent.putExtra(GraphPropertiesActivity.ARG_GRAPH_ID, graphId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_properties);

        Long graphId = (Long) getIntent().getExtras().get(ARG_GRAPH_ID);
        if (graphId == null) {
            graph = new Graph();
            graph.set_id(System.nanoTime());
            setTitle("New");
        } else {
            graph = getDAO().loadGraph(graphId);
            setTitle(R.string.action_edit);
        }

        graphNameEditText = (EditText) findViewById(R.id.graphName);
        graphNameEditText.setText(graph.name == null ? "" : graph.name);

        final List<GraphColumn> columns = getDAO().getColumns(graphId);

        ArrayAdapter<GraphColumn> adapter = new ArrayAdapter<GraphColumn>(this, R.layout.fragment_graph_column_info, columns.toArray(new GraphColumn[columns.size()])) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final GraphColumn graphColumn = columns.get(position);
                View view = getLayoutInflater().inflate(R.layout.fragment_graph_column_info, null);

                Button edtiGraphButton = (Button) view.findViewById(R.id.edit_column);
                edtiGraphButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GraphColumnActivity.start(GraphPropertiesActivity.this, graph, graphColumn);
                    }

                });

                TextView graphColumnTextView = (TextView) view.findViewById(R.id.graph_column_description);
                graphColumnTextView.setText(String.format("%s [%s]", graphColumn.getName(), graphColumn.getUnit()));
                edtiGraphButton.setText(R.string.change);
                //edtiGraphButton.setText(R.string.enable);

                return view;
            }

        };

        fieldsListView = (ListView) findViewById(R.id.fields);
        fieldsListView.setAdapter(adapter);

        setupGraphTypeRadioButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph_properties, menu);
        return true;
    }

    private void setupGraphTypeRadioButtons() {
        int[] graphTypeRadioButtonIds = new int[]{
                R.id.graph_properties__graph_type_1,
                R.id.graph_properties__graph_type_2,
                R.id.graph_properties__graph_type_3,
        };
        RadioButton[] graphTypeRadioButtons = new RadioButton[graphTypeRadioButtonIds.length];
        if (graphTypeRadioButtonIds.length != GraphType.values().length) {
            throw new Error("Invalid # of radio buttons");
        }
        for (int i = 0; i < GraphType.values().length; i++) {
            final GraphType graphType = GraphType.values()[i];

            graphTypeRadioButtons[i] = (RadioButton) findViewById(graphTypeRadioButtonIds[i]);
            graphTypeRadioButtons[i].setText(graphType.getDescription());
            graphTypeRadioButtons[i].setChecked(graph.type == graphType.getType());

            graphTypeRadioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        graph.type = graphType.getType();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_graph) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveGraphProperties(MenuItem item) {
        graph.name = graphNameEditText.getText().toString();

        graph.goal = null;

        getDAO().save(graph);

        GraphActivity.start(this, graph._id);

        Toast.makeText(this, "Graph saved", Toast.LENGTH_SHORT).show();
    }

}