package info.puzz.graphanything.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models.FormatVariant;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphUnitType;
import info.puzz.graphanything.models.format.FormatException;

public class GraphColumnActivity extends BaseActivity {

    private static final String ARG_GRAPH_ID = "gid";
    private static final String COLUMN_NO = "colno";

    private EditText unitOfMeasurementEditText;
    private EditText unitOfMeasurementField;
    private EditText goalEditText;

    private Graph graph;

    public static void start(BaseActivity activity, int graphId, int columndNo) {
        Intent intent = new Intent(activity, GraphColumnActivity.class);
        intent.putExtra(GraphColumnActivity.ARG_GRAPH_ID, graphId);
        intent.putExtra(GraphColumnActivity.COLUMN_NO, columndNo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_column);

        Long graphId = (Long) getIntent().getExtras().get(ARG_GRAPH_ID);
        graph = getDAO().loadGraph(graphId);

        unitOfMeasurementEditText = (EditText) findViewById(R.id.graph__unit_of_measurement);
        unitOfMeasurementField = (EditText) findViewById(R.id.graph__unit_of_measurement);
        goalEditText = (EditText) findViewById(R.id.goal);

        unitOfMeasurementEditText.setText(graph.unit == null ? "" : graph.unit);
        goalEditText.setText(graph.calculateGoal() ? graph.getGraphUnitType().format(graph.goal, FormatVariant.LONG) : "");

        setupUnitTypeRadioButtons();
    }

    public void onSave(MenuItem item) {
        graph.unit = unitOfMeasurementEditText.getText().toString();
        String goalStr = goalEditText.getText().toString().trim();

        // TODO: Update column definition, not graph!

        if (goalStr != null && goalStr.length() > 0) {
            try {
                graph.goal = graph.getGraphUnitType().parse(goalStr);
            } catch (FormatException e) {
                new AlertDialog.Builder(this)
                        .setTitle("Invalid value")
                        .setMessage(e.getMessage())
                        .setNeutralButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return;
            }
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

}
