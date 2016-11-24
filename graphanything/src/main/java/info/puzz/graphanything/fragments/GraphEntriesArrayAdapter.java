package info.puzz.graphanything.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.activities.BaseActivity;
import info.puzz.graphanything.activities.GraphEntryActivity;
import info.puzz.graphanything.databinding.GraphEntryBinding;
import info.puzz.graphanything.models2.FormatVariant;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphEntry;
import info.puzz.graphanything.utils.StringUtils;
import info.puzz.graphanything.utils.TimeUtils;

public class GraphEntriesArrayAdapter extends ArrayAdapter<GraphEntry> {

    private static final String TAG = GraphEntriesArrayAdapter.class.getSimpleName();

    private final Context context;
    private final GraphEntry[] values;
    private final Graph graph;
    private final GraphColumn column;

    public GraphEntriesArrayAdapter(Context context, Graph graph, GraphEntry[] values, Map<Integer, GraphColumn> columns) {
        super(context, R.layout.graph, values);
        this.context = context;
        this.graph = graph;
        this.values = values;
        this.column = columns.get(0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GraphEntryBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.graph_entry, parent, false);
        } else {
            binding = DataBindingUtil.findBinding(convertView);
        }

        final GraphEntry graphEntry = values[position];

        binding.graphValueTitle.setText(column.formatValueWithUnit(graphEntry.get(0), FormatVariant.LONG));
        binding.graphValueSubtitleCreated.setText(TimeUtils.YYYYMMDDHHMMSS_FORMATTER.format(new Timestamp(graphEntry.created)));
        binding.entryComment.setText(StringUtils.isEmpty(graphEntry.getComment()) ? "..." : StringUtils.ellipses(graphEntry.getComment().replace("\n", " "), 40));

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphEntryActivity.start((BaseActivity) context, graph._id, graphEntry);
            }
        });

        return binding.getRoot();
    }
}

