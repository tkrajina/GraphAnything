package info.puzz.graphanything.fragments;

import android.content.Context;
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
import info.puzz.graphanything.models2.FormatVariant;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphInfo;
import info.puzz.graphanything.models2.GraphEntry;
import info.puzz.graphanything.utils.StringUtils;
import info.puzz.graphanything.utils.TimeUtils;

public class GraphEntriesArrayAdapter extends ArrayAdapter<GraphEntry> {
    private final Context context;
    private final GraphEntry[] values;
    private final GraphInfo graph;
    private final GraphColumn column;

    public GraphEntriesArrayAdapter(Context context, GraphInfo graph, GraphEntry[] values, Map<Integer, GraphColumn> columns) {
        super(context, R.layout.graph, values);
        this.context = context;
        this.graph = graph;
        this.values = values;
        this.column = columns.get(0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.graph_entry, parent, false);

        final GraphEntry graphEntry = values[position];

        TextView titleView = (TextView) rowView.findViewById(R.id.graph_value_title);
        titleView.setText(column.formatValueWithUnit(graphEntry.get(0), FormatVariant.LONG));

        TextView valueCreatedTextView = (TextView) rowView.findViewById(R.id.graph_value_subtitle_created);
        valueCreatedTextView.setText(TimeUtils.YYYYMMDDHHMMSS_FORMATTER.format(new Timestamp(graphEntry.created)));

        TextView commentTextView = (TextView) rowView.findViewById(R.id.entry_comment);
        commentTextView.setText(StringUtils.isEmpty(graphEntry.getComment()) ? "..." : graphEntry.getComment());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphEntryActivity.start((BaseActivity) context, graph._id, graphEntry);
            }
        });

        return rowView;
    }
}

