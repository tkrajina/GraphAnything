package info.puzz.graphanything.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import info.puzz.graphanything.listeners.CalendarChangeListener;

/**
 * Created by puzz on 09.03.15..
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Calendar cal;
    private CalendarChangeListener listener;

    public DatePickerFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (cal == null) {
            return;
        }
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (listener != null) {
            listener.onCalendarChanged(cal);
        }
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    public void setListener(CalendarChangeListener listener) {
        this.listener = listener;
    }
}
