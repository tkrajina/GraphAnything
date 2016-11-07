package info.puzz.graphanything.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

/**
 * Created by puzz on 07/11/2016.
 */

public class TimerSoundPlayer extends BroadcastReceiver {

    private static final String TAG = TimerSoundPlayer.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received");
        // send the tone to the "alarm" stream (classic beeps go there) with 50% volume
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); // 200 is duration in ms
    }
}
