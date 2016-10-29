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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphColumn;
import info.puzz.graphanything.models.GraphType;
import info.puzz.graphanything.utils.AssertUtils;
import info.puzz.graphanything.utils.ListViewUtils;


public class GraphEditActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "graph_id";
    private static final String ARG_GRAPH = "graph";
    private static final String ARG_GRAPH_COLUMNS = "graph_columns";

    private EditText graphNameEditText;
    private ListView fieldsListView;

    private Graph graph;
    private Map<Integer, GraphColumn> columnsByColumnNumbers;

    public static void start(BaseActivity activity, Long graphId) {
        Intent intent = new Intent(activity, GraphEditActivity.class);
        intent.putExtra(GraphEditActivity.ARG_GRAPH_ID, graphId);
        activity.startActivity(intent);
    }

    public static void start(BaseActivity activity, Graph graph, Map<Integer, GraphColumn> columns) {
        AssertUtils.assertNotNull(graph);
        AssertUtils.assertNotNull(columns);

        Intent intent = new Intent(activity, GraphEditActivity.class);
        intent.putExtra(ARG_GRAPH, graph);
        intent.putExtra(ARG_GRAPH_COLUMNS, (Serializable) columns);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_edit);

        graph = (Graph) getIntent().getSerializableExtra(ARG_GRAPH);
        if (graph == null) {
            Long graphId = (Long) getIntent().getExtras().get(ARG_GRAPH_ID);
            if (graphId == null) {
                graph = new Graph();
                graph.set_id(System.nanoTime());
                columnsByColumnNumbers = new HashMap<Integer, GraphColumn>();
            } else {
                graph = getDAO().loadGraph(graphId);
                columnsByColumnNumbers = getDAO().getColumnsByColumnNo(graphId);
                setTitle(R.string.action_edit);
            }
        } else {
            columnsByColumnNumbers = (Map<Integer, GraphColumn>) getIntent().getExtras().getSerializable(ARG_GRAPH_COLUMNS);
            AssertUtils.assertNotNull(columnsByColumnNumbers);
        }

        setTitle(R.string.edit_graph);

        if (columnsByColumnNumbers == null || columnsByColumnNumbers.size() == 0) {
            columnsByColumnNumbers = getDAO().getColumnsByColumnNo(graph._id);
        }

        graphNameEditText = (EditText) findViewById(R.id.graphName);
        graphNameEditText.setText(graph.name == null ? "" : graph.name);

        reloadFields();

        setupGraphTypeRadioButtons();
    }

    private void reloadFields() {
        final List<GraphColumn> columns = new ArrayList<>(columnsByColumnNumbers.size());
        for (int columnNo = 0; columnNo < GraphColumn.COLUMNS_NO; columnNo++) {
            if (columnsByColumnNumbers.containsKey(columnNo)) {
                columns.add(columnsByColumnNumbers.get(columnNo));
            }
        }

        final GraphColumn[] columnsArray = columns.toArray(new GraphColumn[columns.size()]);
        ArrayAdapter<GraphColumn> adapter = new ArrayAdapter<GraphColumn>(this, R.layout.fragment_graph_column_info, columnsArray) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final GraphColumn graphColumn = getItem(position);

                View view = convertView;
                if (view == null) {
                    view = getLayoutInflater().inflate(R.layout.fragment_graph_column_info, null);
                }

                Button editGraphButton = (Button) view.findViewById(R.id.edit_column);

                if (graphColumn == null) {
                    editGraphButton.setText(R.string.new_column);
                } else {
                    editGraphButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GraphColumnActivity.start(GraphEditActivity.this, graph, columnsByColumnNumbers, graphColumn.getColumnNo());
                        }

                    });

                    TextView graphColumnTextView = (TextView) view.findViewById(R.id.graph_column_description);
                    graphColumnTextView.setText(String.format("%s [%s]", graphColumn.getName(), graphColumn.getUnit()));
                    editGraphButton.setText(R.string.change);
                    //editGraphButton.setText(R.string.enable);
                }
                return view;
            }

        };

        fieldsListView = (ListView) findViewById(R.id.fields);
        fieldsListView.setAdapter(adapter);
        ListViewUtils.setListViewHeightBasedOnChildren(fieldsListView);
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

        Map<Integer, GraphColumn> currentColumns = getDAO().getColumnsByColumnNo(graph._id);
        for (Map.Entry<Integer, GraphColumn> e : currentColumns.entrySet()) {
            GraphColumn column = columnsByColumnNumbers.get(e.getKey());
            if (columnsByColumnNumbers.containsKey(e.getKey())) {
                column.setGraphId(graph._id);
                getDAO().save(column);
            } else {
                getDAO().deleteGraph(column);
            }
        }
        getDAO().save(graph);

        GraphActivity.start(this, graph._id);

        Toast.makeText(this, "Graph saved", Toast.LENGTH_SHORT).show();
    }

}