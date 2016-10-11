package info.puzz.graphanything.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import info.puzz.graphanything.R;
import info.puzz.graphanything.dao.DAO;

/**
 * Created by puzz on 05/10/16.
 */
public abstract class BaseActivity extends ActionBarActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    DAO dao;

    public DAO getDAO() {
        if (this.dao == null) {
            this.dao = new DAO(this);
            this.dao.open();
        }
        return this.dao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Opening connection");

        super.onCreate(savedInstanceState);

        if (!this.getClass().equals(GraphListActivity.class)) {
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "Closing connection");
        super.onPause();

        if (this.dao != null) {
            this.dao.close();
            this.dao = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                GraphListActivity.start(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
