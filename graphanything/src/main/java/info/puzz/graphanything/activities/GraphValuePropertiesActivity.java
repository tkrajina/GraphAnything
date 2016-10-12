package info.puzz.graphanything.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import info.puzz.graphanything.listeners.CalendarChangeListener;
import info.puzz.graphanything.fragments.DatePickerFragment;
import info.puzz.graphanything.R;
import info.puzz.graphanything.fragments.TimePickerFragment;
import info.puzz.graphanything.models.GraphValue;
import info.puzz.graphanything.utils.Formatters;


public class GraphValuePropertiesActivity extends BaseActivity implements CalendarChangeListener {

    private static final String ARG_GRAPH_VALUE_ID = "value_id";
    private Calendar cal;
    private GraphValue graphValue;

    /**
     * Utility to start this activity from another one.
     */
    public static void start(ActionBarActivity activity, long graphValueId) {
        Intent intent = new Intent(activity, GraphValuePropertiesActivity.class);
        intent.putExtra(GraphValuePropertiesActivity.ARG_GRAPH_VALUE_ID, graphValueId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_value_properties);

        Long graphValueId = (Long) getIntent().getExtras().get(ARG_GRAPH_VALUE_ID);

        graphValue = getDAO().getValue(graphValueId);

        cal = GregorianCalendar.getInstance();
        cal.setTime(new Date(graphValue.created));

        TextView valueTextView = (TextView) findViewById(R.id.value);
        valueTextView.setText("" + graphValue.value);

        redrawDateTime();

        setTitle("Edit value");
    }

    private void redrawDateTime() {
        TextView dateTextView = (TextView) findViewById(R.id.date);
        TextView timeTextView = (TextView) findViewById(R.id.time);
        dateTextView.setText(formatDate());
        timeTextView.setText(formatTime());
    }

    private String formatTime() {
        return Formatters.DATE_FORMAT.format(cal.getTime());
    }

    private String formatDate() {
        return Formatters.TIME_FORMAT.format(cal.getTime());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph_value_properties, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setCal(cal);
        newFragment.setListener(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setCal(cal);
        newFragment.setListener(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onSaveValue(View view) {
        TextView valueTextView = (TextView) findViewById(R.id.value);
        double value = Double.valueOf(valueTextView.getText().toString());

        graphValue.value = value;
        graphValue.created = cal.getTimeInMillis();
        getDAO().updateGraphValue(graphValue);

        GraphActivity.start(this, graphValue.graphId);

        Toast.makeText(this, "Value updated", Toast.LENGTH_SHORT).show();
    }

    public void deleteGraphValue(MenuItem item) {
        getDAO().deleteGraphValue(graphValue);

        GraphActivity.start(this, graphValue.graphId);
    }

    @Override
    public void onCalendarChanged(Calendar cal) {
        redrawDateTime();
    }
}
