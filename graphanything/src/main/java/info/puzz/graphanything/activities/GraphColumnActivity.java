package info.puzz.graphanything.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.databinding.ActivityGraphColumnBinding;
import info.puzz.graphanything.models2.FormatVariant;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.enums.GraphType;
import info.puzz.graphanything.models2.enums.GraphUnitType;
import info.puzz.graphanything.models2.format.FormatException;
import info.puzz.graphanything.utils.DialogUtils;
import info.puzz.graphanything.utils.StringUtils;

public class GraphColumnActivity extends BaseActivity {

    private static final String ARG_GRAPH = "graph";
    private static final String ARG_GRAPH_COLUMNS = "graph_columns";
    private static final String ARG_GRAPH_COLUMN_NO = "graph_column_no";

    private Graph graph;
    private int graphColumnNo;

    private Map<Integer, GraphColumn> graphColumns;
    ActivityGraphColumnBinding binding;


    public static void start(BaseActivity activity, Graph graph, Map<Integer, GraphColumn> columns, int column) {
        Assert.assertNotNull(graph);
        Assert.assertNotNull(columns);
        Assert.assertTrue(columns.containsKey(column));

        Intent intent = new Intent(activity, GraphColumnActivity.class);
        intent.putExtra(ARG_GRAPH, graph);
        intent.putExtra(ARG_GRAPH_COLUMNS, (Serializable) columns);
        intent.putExtra(ARG_GRAPH_COLUMN_NO, column);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disallowWalkAway(R.string.no_changes_saved_message);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_graph_column);

        graph = (Graph) getIntent().getExtras().get(ARG_GRAPH);
        graphColumns = (Map<Integer, GraphColumn>) getIntent().getExtras().get(ARG_GRAPH_COLUMNS);
        Integer graphColumnNo = (Integer) getIntent().getExtras().get(ARG_GRAPH_COLUMN_NO);

        Assert.assertNotNull(graphColumnNo);
        Assert.assertTrue(graphColumns.containsKey(graphColumnNo));

        this.graphColumnNo = graphColumnNo;

        Assert.assertNotNull(graph); // TODO
        Assert.assertNotNull(graphColumns);

        binding.actionGraphPropertiesTypeGroup.setVisibility(graphColumnNo == 0 ? View.VISIBLE : View.GONE);

        binding.unitOfMeasurement.setText(getCurrentGraphColumn().unit == null ? "" : getCurrentGraphColumn().unit);
        binding.goal.setText(getCurrentGraphColumn().calculateGoal() ? getCurrentGraphColumn().getGraphUnitType().format(getCurrentGraphColumn().goal, FormatVariant.LONG) : "");
        binding.columnName.setText(getCurrentGraphColumn().name);

        setupUnitTypeRadioButtons();
        setupGraphTypeRadioButtons();
    }

    private GraphColumn getCurrentGraphColumn() {
        return graphColumns.get(graphColumnNo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph_column, menu);

        // Cannot delete first column
        menu.findItem(R.id.action_delete).setEnabled(canBeRemoved());

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
            graphTypeRadioButtons[i].setChecked(getCurrentGraphColumn().getGraphType() == graphType);

            graphTypeRadioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        getCurrentGraphColumn().type = graphType.getType();
                    }
                }
            });
        }
    }

    private boolean canBeRemoved() {
        return graphColumnNo != 0;
    }

    public void onSave(MenuItem item) {
        getCurrentGraphColumn().unit = binding.unitOfMeasurement.getText().toString();
        getCurrentGraphColumn().name = binding.columnName.getText().toString();
        String goalStr = binding.goal.getText().toString().trim();

        if (StringUtils.isEmpty(goalStr)) {
            getCurrentGraphColumn().goal = null;
        } else {
            try {
                getCurrentGraphColumn().goal = getCurrentGraphColumn().getGraphUnitType().parse(goalStr);
            } catch (FormatException e) {
                DialogUtils.showWarningDialog(this, "Invalid goal value", e.getMessage());
                return;
            }
        }

        Toast.makeText(GraphColumnActivity.this, "Column changed", Toast.LENGTH_SHORT).show();
        allowWalkAway();
        GraphEditActivity.start(this, graph, graphColumns);
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
            unitTypeRadioButtons[i].setChecked(getCurrentGraphColumn().unitType == graphUnitType.getType());

            unitTypeRadioButtons[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        getCurrentGraphColumn().unitType = graphUnitType.getType();
                        reloadUnitOfMeasurementField(graphUnitType.getType());
                    }
                }
            });
        }
        reloadUnitOfMeasurementField(getCurrentGraphColumn().unitType);
    }

    private void reloadUnitOfMeasurementField(int graphType) {
        int visibility = graphType == GraphUnitType.TIMER.getType() ? View.GONE : View.VISIBLE;
        binding.unitOfMeasurement.setVisibility(visibility);
        binding.unitOfMeasurementLabel.setVisibility(visibility);
    }

    public void onDeleteColumn(MenuItem item) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Assert.assertTrue(canBeRemoved());

                    graphColumns.remove(graphColumnNo);
                    GraphEditActivity.start(GraphColumnActivity.this, graph, graphColumns);
                    Toast.makeText(GraphColumnActivity.this, "Column deleted", Toast.LENGTH_SHORT).show();
                }
            }
        };

        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_column)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }
}
