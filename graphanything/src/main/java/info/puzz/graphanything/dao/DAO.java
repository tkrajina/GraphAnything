package info.puzz.graphanything.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphValue;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DAO {

    private static final String TAG = DAO.class.getSimpleName();

    private final Context mCtx;

    private DatabaseOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

    public void updateGraphValue(GraphValue graphValue) {
        cupboard().withDatabase(mDb).put(graphValue);
    }

    public void save(Graph graph) {
        cupboard().withDatabase(mDb).put(graph);
    }

    public void deleteGraphValue(GraphValue graphValue) {
        cupboard().withDatabase(mDb).delete(graphValue);
    }

    public void deleteGraphValues(long graphId) throws SQLException {
        cupboard().withDatabase(mDb).delete(GraphValue.class, "graphId = ?", String.valueOf(graphId));
    }

    public DAO(Context ctx) {
        this.mCtx = ctx;
    }

    public DAO open() throws SQLException {
        mDbHelper = new DatabaseOpenHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void deleteGraph(Graph graph) {
        cupboard().withDatabase(mDb).delete(graph);
    }

    public List<Graph> fetchAllGraphs() {
        ArrayList<Graph> result = new ArrayList<>();
        Cursor graphs = cupboard().withDatabase(mDb).query(Graph.class).getCursor();

        QueryResultIterable<Graph> itr = null;
        try {
            // Iterate books
            itr = cupboard().withDatabase(mDb).query(Graph.class).query();
            for (Graph graph : itr) {
                result.add(graph);
                // do something with book
            }
        } finally {
            // close the cursor
            itr.close();
        }

        return result;
    }

    public Graph loadGraph(long id) throws SQLException {
        return cupboard().withDatabase(mDb).get(Graph.class, id);
    }

    public void addValue(long graphId, double value) {
        GraphValue graphValue = new GraphValue();
        graphValue.graphId = graphId;
        graphValue.value = value;
        graphValue.created = System.currentTimeMillis();

        addValue(graphValue);
    }

    public void addValue(GraphValue graphValue) {
        cupboard().withDatabase(mDb).put(graphValue);
    }

    public QueryResultIterable<GraphValue> getValuesByCreatedAscCursor(long graphId) {
        return cupboard().withDatabase(mDb)
                .query(GraphValue.class)
                .withSelection("graphId = ?", String.valueOf(graphId))
                .orderBy("created")
                .query();
    }

    public List<GraphValue> getValues(long graphId) {
        ArrayList<GraphValue> result = new ArrayList<>();

        QueryResultIterable<GraphValue> i = null;
        try {
            i = getValuesByCreatedAscCursor(graphId);
            for (GraphValue graphValue : i) {
                result.add(graphValue);
            }
        } finally {
            i.close();
        }

        return result;
    }

    public GraphValue getValue(long graphValueId) {
        return cupboard().withDatabase(mDb).get(GraphValue.class, graphValueId);
    }

}
