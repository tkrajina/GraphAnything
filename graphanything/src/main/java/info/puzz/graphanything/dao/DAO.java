package info.puzz.graphanything.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphColumn;
import info.puzz.graphanything.models.GraphEntry;
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

    public void save(Graph graph) {
        cupboard().withDatabase(mDb).put(graph);
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

    public void deleteGraph(Graph graph) {
        cupboard().withDatabase(mDb).delete(graph);
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

    public List<GraphColumn> getColumns(Long graphId) {
        Set<Integer> columnNumbers = new HashSet<>();
        List<GraphColumn> columns = cupboard().withDatabase(mDb)
                .query(GraphColumn.class)
                .withSelection("graphId = ?", String.valueOf(graphId))
                .orderBy("columnNo")
                .list();

        for (GraphColumn column : columns) {
            columnNumbers.add(column.getColumnNo());
        }

        for (int i = 0; i < GraphEntry.COLUMNS_NO; i++) {
            if (!columnNumbers.contains(i)) {
                GraphColumn missingColumn = new GraphColumn()
                        .setGraphId(graphId)
                        .setColumnNo(i);
                columns.add(missingColumn);
            }
        }

        return columns;
    }
}
