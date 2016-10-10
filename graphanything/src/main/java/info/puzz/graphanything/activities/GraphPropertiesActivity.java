package info.puzz.graphanything.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphType;
import info.puzz.graphanything.models.GraphUnitType;


public class GraphPropertiesActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "graph_id";

    private EditText graphNameEditText;
    private EditText unitOfMeasurementEditText;
    private EditText unitOfMeasurementField;
    private EditText goalEditText;

    private Graph graph;

    public static void start(ActionBarActivity activity, Long graphId) {
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
        } else {
            graph = getDAO().loadGraph(graphId);
        }

        graphNameEditText = (EditText) findViewById(R.id.graphName);
        unitOfMeasurementEditText = (EditText) findViewById(R.id.graph__unit_of_measurement);
        unitOfMeasurementField = (EditText) findViewById(R.id.graph__unit_of_measurement);
        goalEditText = (EditText) findViewById(R.id.graph__goal);

        graphNameEditText.setText(graph.name == null ? "" : graph.name);
        unitOfMeasurementEditText.setText(graph.unit == null ? "" : graph.unit);
        goalEditText.setText(graph.calculateGoal() ? String.valueOf(graph.goal) : "");

        setupUnitTypeRadioButtons();
        setupGraphTypeRadioButtons();

        setTitle("Edit: " + graph.name);
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

    private void setupUnitTypeRadioButtons() {
        int[] unitTypeRadioButtonIds = new int[]{R.id.graph_properties__unit_type_1, R.id.graph_properties__unit_type_2};
        RadioButton[] unitTypeRadioButtons = new RadioButton[unitTypeRadioButtonIds.length];
        if (unitTypeRadioButtonIds.length != GraphUnitType.values().length) {
            throw new Error("Invalid # of radio buttons");
        }

        for (int i = 0; i < GraphUnitType.values().length; i++) {
            final GraphUnitType graphUnitType = GraphUnitType.values()[i];

            unitTypeRadioButtons[i] = (RadioButton) findViewById(unitTypeRadioButtonIds[i]);
            unitTypeRadioButtons[i].setText(graphUnitType.getDescription());
            unitTypeRadioButtons[i].setChecked(graph.unitType == graphUnitType.getType());

            unitTypeRadioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        graph.unitType = graphUnitType.getType();
                        reloadUnitOfMeasurementField(graphUnitType.getType());
                    }
                }
            });
        }
        reloadUnitOfMeasurementField(graph.unitType);
    }

    private void reloadUnitOfMeasurementField(int graphType) {
        unitOfMeasurementField.setEnabled(graphType != GraphUnitType.TIMER.getType());
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

    public void saveGraphProperties(View view) {

        graph.name = graphNameEditText.getText().toString();
        graph.unit = unitOfMeasurementEditText.getText().toString();

        String goalStr = goalEditText.getText().toString();
        graph.goal = null;
        if (goalStr != null && goalStr.length() > 0) {
            graph.goal = Double.parseDouble(goalStr);
        }

        getDAO().save(graph);

        GraphActivity.start(this, graph._id);

        Toast.makeText(this, "Graph saved", Toast.LENGTH_SHORT).show();
    }

}