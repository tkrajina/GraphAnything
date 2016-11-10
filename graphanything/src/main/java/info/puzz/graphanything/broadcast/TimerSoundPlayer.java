package info.puzz.graphanything.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import info.puzz.graphanything.services.GraphAlarms;

public class TimerSoundPlayer extends BroadcastReceiver {

    private static final String TAG = TimerSoundPlayer.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received");
        GraphAlarms.alarm(context, intent);
    }
}
