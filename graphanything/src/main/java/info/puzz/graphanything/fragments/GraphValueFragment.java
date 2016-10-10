package info.puzz.graphanything.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import info.puzz.graphanything.activities.BaseActivity;
import info.puzz.graphanything.dao.DAO;
import info.puzz.graphanything.activities.GraphValuesActivity;
import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphValue;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class GraphValueFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private long graphId;
    private List<GraphValue> graphValues;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GraphValueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        GraphValuesActivity activity = (GraphValuesActivity) getActivity();
        graphId = activity.getGraphId();

        DAO DAO = ((BaseActivity) getActivity()).getDAO();

        Graph graph = DAO.loadGraph(graphId);
        graphValues = DAO.getValues(graphId);

        setListAdapter(
                new GraphValueArrayAdapter(
                        getActivity(),
                        graph,
                        graphValues.toArray(new GraphValue[graphValues.size()])));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
            mListener.onFragmentInteraction(graphValues.get(position)._id);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(long graphValueId);
    }

}
