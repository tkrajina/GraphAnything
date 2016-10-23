package info.puzz.graphanything.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import info.puzz.graphanything.R;
import info.puzz.graphanything.activities.BaseActivity;
import info.puzz.graphanything.activities.GraphColumnActivity;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphColumn;
import lombok.Getter;


public class GraphColumnInfoFragment extends Fragment {

    private static final String ARG_GRAPH = "g";
    private static final String ARG_GRAPH_COLUMN = "gcol";

    @Getter
    private GraphColumn graphColumn;
    private Graph graph;

    private Button edtiGraphButton;

    public static GraphColumnInfoFragment newInstance(Graph graph, GraphColumn graphColumn) {
        GraphColumnInfoFragment fragment = new GraphColumnInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_GRAPH, graph);
        bundle.putSerializable(ARG_GRAPH_COLUMN, graphColumn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.graph = (Graph) getArguments().getSerializable(ARG_GRAPH);
        this.graphColumn = (GraphColumn) getArguments().getSerializable(ARG_GRAPH_COLUMN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_column_info, container, false);

        edtiGraphButton = (Button) view.findViewById(R.id.edit_column);
        edtiGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectColumn();
            }
        });

        return view;
    }

    public void onSelectColumn() {
        GraphColumnActivity.start((BaseActivity) this.getActivity(), graph, graphColumn);
    }

}
