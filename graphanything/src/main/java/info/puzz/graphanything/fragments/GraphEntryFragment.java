package info.puzz.graphanything.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import info.puzz.graphanything.activities.BaseActivity;
import info.puzz.graphanything.activities.GraphEntriesActivity;
import info.puzz.graphanything.dao.DAO;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphEntry;


/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GraphEntryFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private long graphId;
    private List<GraphEntry> graphEntries;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GraphEntryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        GraphEntriesActivity activity = (GraphEntriesActivity) getActivity();
        graphId = activity.getGraphId();

        DAO dao = ((BaseActivity) getActivity()).getDAO();

        Graph graph = dao.loadGraph(graphId);
        graphEntries = dao.getEntriesByCreatedDesc(graphId);

        Map<Integer, GraphColumn> columns = dao.getColumnsByColumnNo(graphId);

        setListAdapter(
                new GraphEntriesArrayAdapter(
                        getActivity(),
                        graph,
                        graphEntries.toArray(new GraphEntry[graphEntries.size()]),
                        columns));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGraphColumnSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            mListener.onFragmentInteraction(graphEntries.get(position));
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(GraphEntry graphEntry);
    }

}
