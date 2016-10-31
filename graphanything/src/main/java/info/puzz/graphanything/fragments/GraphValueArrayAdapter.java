package info.puzz.graphanything.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import info.puzz.graphanything.R;
import info.puzz.graphanything.activities.BaseActivity;
import info.puzz.graphanything.activities.GraphEntryActivity;
import info.puzz.graphanything.models.FormatVariant;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphEntry;
import info.puzz.graphanything.utils.TimeUtils;

public class GraphValueArrayAdapter extends ArrayAdapter<GraphEntry> {
    private final Context context;
    private final GraphEntry[] values;
    private final Graph graph;

    public GraphValueArrayAdapter(Context context, Graph graph,  GraphEntry[] values) {
        super(context, R.layout.graph, values);
        this.context = context;
        this.graph = graph;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.graph_value, parent, false);

        final GraphEntry graphEntry = values[position];

        TextView titleView = (TextView) rowView.findViewById(R.id.graph_value_title);
        titleView.setText(graph.formatValueWithUnit(graphEntry.get(0), FormatVariant.LONG));

        TextView valueCreatedTextView = (TextView) rowView.findViewById(R.id.graph_value_subtitle_created);
        valueCreatedTextView.setText(TimeUtils.formatTimeAgoString(graphEntry.created));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphEntryActivity.start((BaseActivity) context, graph._id, graphEntry);
            }
        });

        return rowView;
    }
}

