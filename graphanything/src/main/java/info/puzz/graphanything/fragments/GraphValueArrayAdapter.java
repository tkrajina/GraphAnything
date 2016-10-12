package info.puzz.graphanything.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import info.puzz.graphanything.R;
import info.puzz.graphanything.activities.GraphValuePropertiesActivity;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphValue;
import info.puzz.graphanything.utils.TimeUtils;

public class GraphValueArrayAdapter extends ArrayAdapter<GraphValue> {
    private final Context context;
    private final GraphValue[] values;
    private final Graph graph;

    public GraphValueArrayAdapter(Context context, Graph graph,  GraphValue[] values) {
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

        final GraphValue graphValue = values[position];

        TextView titleView = (TextView) rowView.findViewById(R.id.graph_value_title);
        titleView.setText(graph.formatValueWithUnit(graphValue, false));

        TextView valueCreatedTextView = (TextView) rowView.findViewById(R.id.graph_value_subtitle_created);
        valueCreatedTextView.setText(TimeUtils.formatTimeAgoString(graphValue.created));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphValuePropertiesActivity.start((android.support.v7.app.ActionBarActivity) context, graphValue._id);
            }
        });

        return rowView;
    }
}

