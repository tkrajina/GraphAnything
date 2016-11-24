package info.puzz.graphanything.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphEntry;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DAO {

    private static final String TAG = DAO.class.getSimpleName();

    private final Context mCtx;

    private DatabaseOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

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

    public void save(Object entity) {
        cupboard().withDatabase(mDb).put(entity);
    }

    public void delete(Object entity) {
        cupboard().withDatabase(mDb).delete(entity);
    }

    public List<Graph> getGraphsByTimerActiveAndUpdatedDesc() {
        return cupboard().withDatabase(mDb)
                .query(Graph.class)
                .orderBy("-timerStarted, -lastValueCreated")
                .list();
    }

    public Graph loadGraph(long id) throws SQLException {
        return cupboard().withDatabase(mDb).get(Graph.class, id);
    }

    public List<GraphEntry> getEntriesByCreatedAsc(long graphId) {
        return cupboard().withDatabase(mDb)
                .query(GraphEntry.class)
                .withSelection("graphId = ?", String.valueOf(graphId))
                .orderBy("created")
                .list();
    }

    public List<GraphEntry> getEntriesByCreatedDesc(long graphId) {
        return cupboard().withDatabase(mDb)
                .query(GraphEntry.class)
                .withSelection("graphId = ?", String.valueOf(graphId))
                .orderBy("-created")
                .list();
    }

    public GraphEntry getLatestEntry(long graphId) {
        List<GraphEntry> res = cupboard().withDatabase(mDb)
                .query(GraphEntry.class)
                .withSelection("graphId = ?", String.valueOf(graphId))
                .orderBy("-created")
                .limit(1)
                .list();
        if (res == null || res.size() == 0) {
            return null;
        }
        return res.get(0);
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

    public List<GraphColumn> getFirstColumns() {
        return cupboard().withDatabase(mDb)
                .query(GraphColumn.class)
                .withSelection("columnNo = ?", String.valueOf(0))
                .list();
    }

    public Map<Long, GraphColumn> getFirstColumnsByGraphId() {
        HashMap<Long, GraphColumn> res = new HashMap<>();
        for (GraphColumn column : getFirstColumns()) {
            res.put(column.graphId, column);
        }
        return res;
    }

}
