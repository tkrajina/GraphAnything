package info.puzz.graphanything.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphEntry;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DAO {

    private static final String TAG = DAO.class.getSimpleName();

    private final Context mCtx;

    private DatabaseOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

    public void updateGraphEntry(GraphEntry graphEntry) {
        cupboard().withDatabase(mDb).put(graphEntry);
    }

    public void save(Object entity) {
        cupboard().withDatabase(mDb).put(entity);
    }

    public void deleteGraphEntry(GraphEntry graphEntry) {
        cupboard().withDatabase(mDb).delete(graphEntry);
    }

    public void deleteGraphEntries(long graphId) throws SQLException {
        cupboard().withDatabase(mDb).delete(GraphEntry.class, "graphId = ?", String.valueOf(graphId));
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

    public void deleteGraph(Object entity) {
        cupboard().withDatabase(mDb).delete(entity);
    }

    public List<Graph> getGraphsByUpdatedDesc() {
        return cupboard().withDatabase(mDb).query(Graph.class).orderBy("-lastValueCreated").list();
    }

    public Graph loadGraph(long id) throws SQLException {
        return cupboard().withDatabase(mDb).get(Graph.class, id);
    }

    public void addEntry(long graphId, double value) {
        GraphEntry graphValue = new GraphEntry();
        graphValue.graphId = graphId;
        graphValue.set(0, value);
        graphValue.created = System.currentTimeMillis();

        addEntry(graphValue);
    }

    public void addEntry(GraphEntry graphEntry) {
        cupboard().withDatabase(mDb).put(graphEntry);
    }

    public QueryResultIterable<GraphEntry> getEntriesByCreatedAscCursor(long graphId) {
        return cupboard().withDatabase(mDb)
                .query(GraphEntry.class)
                .withSelection("graphId = ?", String.valueOf(graphId))
                .orderBy("created")
                .query();
    }

    public List<GraphEntry> getEntriesByCreatedAsc(long graphId) {
        ArrayList<GraphEntry> result = new ArrayList<>();

        QueryResultIterable<GraphEntry> i = null;
        try {
            i = getEntriesByCreatedAscCursor(graphId);
            for (GraphEntry graphValue : i) {
                result.add(graphValue);
            }
        } finally {
            i.close();
        }

        return result;
    }

    public GraphEntry getEntry(long graphValueId) {
        return cupboard().withDatabase(mDb).get(GraphEntry.class, graphValueId);
    }

    public Map<Integer, GraphColumn> getColumnsByColumnNo(Long graphId) {
        HashMap<Integer, GraphColumn> result = new HashMap<>();
        List<GraphColumn> columns = getColumns(graphId);

        for (GraphColumn column : columns) {
            result.put(column.getColumnNo(), column);
        }

        return result;
    }

    public List<GraphColumn> getColumns(Long graphId) {
        return cupboard().withDatabase(mDb)
                .query(GraphColumn.class)
                .withSelection("graphId = ?", String.valueOf(graphId))
                .orderBy("columnNo")
                .list();
    }
}
