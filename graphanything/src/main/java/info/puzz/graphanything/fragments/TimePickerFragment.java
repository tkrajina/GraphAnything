package info.puzz.graphanything.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

import info.puzz.graphanything.listeners.CalendarChangeListener;

/**
 * Created by puzz on 09.03.15..
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Calendar cal;

    private CalendarChangeListener listener;

    public TimePickerFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hours, minutes, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (cal == null) {
            return;
        }
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
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
