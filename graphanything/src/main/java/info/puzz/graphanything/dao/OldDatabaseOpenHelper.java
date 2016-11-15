package info.puzz.graphanything.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.puzz.graphanything.models.Graph;
import info.puzz.graphanything.models.GraphValue;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

class OldDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = OldDatabaseOpenHelper.class.getSimpleName();

    static {
        // Deprecated:
        cupboard().register(Graph.class);
        cupboard().register(GraphValue.class);
    }

    private static final String DATABASE_NAME = "graphanything2";

    private static final int DATABASE_VERSION = 8;

    OldDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
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

}


