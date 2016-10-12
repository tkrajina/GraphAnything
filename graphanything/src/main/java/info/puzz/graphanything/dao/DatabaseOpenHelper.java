package info.puzz.graphanything.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.List;

import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphValue;
import info.puzz.graphanything.models.format.FormatException;
import info.puzz.graphanything.services.ExportImportUtils;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();

    static {
        // register our models
        cupboard().register(Graph.class);
        cupboard().register(GraphValue.class);
    }

    private static final String DATABASE_NAME = "graphanything2";

    private static final int DATABASE_VERSION = 6;

    DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
        try {
            importSampleGraphs(db);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * onUpgrade method is called when database version changes.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }

    private void importSampleGraphs(SQLiteDatabase db) throws ParseException {
        {
            Graph graph = new Graph();
            graph.name = "EXAMPLE: My weight";
            graph.unit = "kg";
            graph.goal = 80D;
            String data = "2016-8-14T9:27:11|\t95.1\n" +
                    "2016-8-22T8:40:45|\t92.1\n" +
                    "2016-8-31T8:30:12|\t91.1\n" +
                    "2016-9-2T9:55:05|\t90.2\n" +
                    "2016-9-5T9:24:08|\t91.1\n" +
                    "2016-9-7T11:16:06|\t90\n" +
                    "2016-9-8T7:58:01|\t89.1\n" +
                    "2016-9-9T8:31:26|\t89.1\n" +
                    "2016-9-10T7:46:17|\t88.8\n" +
                    "2016-9-12T10:46:49|\t90\n" +
                    "2016-9-14T8:54:21|\t89\n" +
                    "2016-9-15T9:37:12|\t87.9\n" +
                    "2016-9-17T10:04:24|\t88.4\n" +
                    "2016-9-18T9:39:29|\t89.9\n" +
                    "2016-9-20T13:01:02|\t89.6\n" +
                    "2016-9-21T9:47:54|\t88.4\n" +
                    "2016-9-22T7:42:43|\t88\n" +
                    "2016-9-23T7:29:27|\t87.5\n" +
                    "2016-9-24T9:07:50|\t87.3\n" +
                    "2016-9-26T8:56:16|\t88.4\n" +
                    "2016-9-28T9:15:57|\t88.1\n" +
                    "2016-9-29T10:25:58|\t88.5\n" +
                    "2016-10-1T9:03:16|\t87.8\n" +
                    "2016-10-2T9:22:08|\t88\n" +
                    "2016-10-3T10:26:52|\t85.6\n" +
                    "2016-10-5T8:57:07|\t87.6\n" +
                    "2016-10-6T8:01:01|\t87.2\n" +
                    "2016-10-7T6:46:40|\t86.7\n";
            try {
                importData(db, graph, data);
            } catch (FormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void importData(SQLiteDatabase db, Graph graph, String data) throws FormatException {
        cupboard().withDatabase(db).put(graph);

        List<GraphValue> values = ExportImportUtils.importGraph(graph, data);

        long maxTime = 0;
        for (GraphValue value : values) {
            if (value.created > maxTime) {
                maxTime = value.created;
            }
        }

        long timeDelta = System.currentTimeMillis() - maxTime;
        for (GraphValue value : values) {
            value.created -= timeDelta;
        }

        cupboard().withDatabase(db).put(values);
    }
}


