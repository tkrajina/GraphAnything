package info.puzz.graphanything.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.activities.BaseActivity;
import info.puzz.graphanything.activities.GraphActivity;
import info.puzz.graphanything.databinding.GraphBinding;
import info.puzz.graphanything.models2.FormatVariant;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.utils.TimeUtils;

public class GraphArrayAdapter extends ArrayAdapter<Graph> {
    private final Context context;
    private final Graph[] values;

    private Map<Long, GraphColumn> firstColumns;

    public GraphArrayAdapter(Context context, Graph[] values, List<GraphColumn> firstColumns) {
        super(context, R.layout.graph, values);
        this.context = context;
        this.values = values;
        this.firstColumns = new HashMap<>();
        for (GraphColumn column : firstColumns) {
            this.firstColumns.put(column.getGraphId(), column);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GraphBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.graph, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        final Graph graph = values[position];
        binding.setGraph(graph);
        GraphColumn column = firstColumns.get(graph._id);
        Assert.assertNotNull(String.format("No columns for graph %d", graph._id), column);

        binding.icon.setImageResource(graph.getActivityIcon(column));
        binding.graphSubtitleLastValue.setText(column.formatValueWithUnit(graph.lastValue, FormatVariant.SHORT));
        binding.graphSubtitleLastValueCreated.setText(TimeUtils.formatTimeAgoString(graph.lastValueCreated));

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphActivity.start((BaseActivity) context, graph._id, 0);
            }
        });

        return binding.getRoot();
    }
}

