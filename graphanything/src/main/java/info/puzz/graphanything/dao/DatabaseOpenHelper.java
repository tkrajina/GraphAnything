package info.puzz.graphanything.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.List;

import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphEntry;
import info.puzz.graphanything.models2.enums.GraphType;
import info.puzz.graphanything.models2.enums.GraphUnitType;
import info.puzz.graphanything.models2.format.FormatException;
import info.puzz.graphanything.services.ExportImportUtils;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();

    static {
        cupboard().register(Graph.class);
        cupboard().register(GraphEntry.class);
        cupboard().register(GraphColumn.class);
    }

    private static final String DATABASE_NAME = "graphanything.db";

    private static final int DATABASE_VERSION = 5;

    private final Context context;

    DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
            Graph graph = new Graph()
                    .setName("EXAMPLE: My weight");
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
                importData(db, graph, data, "Weight", "kg", GraphType.VALUES, GraphUnitType.UNIT);
            } catch (FormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        {
            Graph graph = new Graph()
                    .setName("EXAMPLE: Project time");
            String data = "2016-10-10T15:18:33|00:37:17\n" +
                    "2016-10-10T15:26:44|00:05:39\n" +
                    "2016-10-10T15:32:37|00:02:31\n" +
                    "2016-10-10T16:22:33|00:11:10\n" +
                    "2016-10-10T16:37:38|00:04:37\n" +
                    "2016-10-10T19:33:35|00:00:02\n" +
                    "2016-10-11T09:42:44|00:49:54\n" +
                    "2016-10-11T11:16:14|00:29:42\n" +
                    "2016-10-11T12:02:48|00:22:03\n" +
                    "2016-10-11T14:01:41|00:40:34\n" +
                    "2016-10-11T15:13:20|00:32:07\n" +
                    "2016-10-11T19:26:06|00:29:33\n" +
                    "2016-10-11T20:00:18|00:29:49\n" +
                    "2016-10-12T09:45:38|00:33:15\n" +
                    "2016-10-12T10:38:58|00:35:19\n" +
                    "2016-10-12T11:48:42|00:37:00\n" +
                    "2016-10-12T14:57:17|00:52:24\n" +
                    "2016-10-12T18:43:16|00:45:10\n";
            try {
                importData(db, graph, data, "Time", "", GraphType.SUM_ALL_PREVIOUS, GraphUnitType.TIMER);
            } catch (FormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void importData(SQLiteDatabase db, Graph graph, String data, String columnName, String columnUnit, GraphType type, GraphUnitType unitType) throws FormatException {
        cupboard().withDatabase(db).put(graph);
        cupboard().withDatabase(db).put(new GraphColumn()
                .setGraphId(graph._id)
                .setName(columnName)
                .setUnit(columnUnit)
                .setType(type.getType())
                .setUnitType(unitType.getType()));

        List<GraphEntry> entries = ExportImportUtils.importGraph(graph, data, unitType);

        long maxTime = 0;
        for (GraphEntry entry : entries) {
            if (entry.created > maxTime) {
                maxTime = entry.created;
            }
        }

        long timeDelta = System.currentTimeMillis() - maxTime;
        for (GraphEntry entry : entries) {
            entry.created += timeDelta;
        }

        cupboard().withDatabase(db).put(entries);
    }
}


