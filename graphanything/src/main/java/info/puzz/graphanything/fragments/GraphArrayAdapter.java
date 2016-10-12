package info.puzz.graphanything.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import info.puzz.graphanything.R;
import info.puzz.graphanything.activities.GraphActivity;
import info.puzz.graphanything.models.FormatVariant;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphUnitType;
import info.puzz.graphanything.utils.TimeUtils;

public class GraphArrayAdapter extends ArrayAdapter<Graph> {
    private final Context context;
    private final Graph[] values;

    public GraphArrayAdapter(Context context, Graph[] values) {
        super(context, R.layout.graph, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.graph, parent, false);

        final Graph graph = values[position];

        ImageView iconTextView = (ImageView) rowView.findViewById(R.id.icon);
        if (graph.unitType == GraphUnitType.TIMER.getType() && graph.timerStarted > 0) {
            iconTextView.setImageResource(R.drawable.ic_timer);
        } else if (graph.calculateGoal()) {
            if (0 < graph.goalEstimateDays && graph.goalEstimateDays < 300) {
                iconTextView.setImageResource(R.drawable.ic_smile);
            } else {
                iconTextView.setImageResource(R.drawable.ic_sad);
            }
        }

        TextView titleView = (TextView) rowView.findViewById(R.id.graph_title);
        titleView.setText(graph.name);

        TextView lastValueTextView = (TextView) rowView.findViewById(R.id.graph_subtitle_last_value);
        lastValueTextView.setText(graph.formatValueWithUnit(graph.lastValue, FormatVariant.SHORT));

        TextView lastValueCreatedTextView = (TextView) rowView.findViewById(R.id.graph_subtitle_last_value_created);
        lastValueCreatedTextView.setText(TimeUtils.formatTimeAgoString(graph.lastValueCreated));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphActivity.start((android.support.v7.app.ActionBarActivity) context, graph._id);
            }
        });

        return rowView;
    }
}

