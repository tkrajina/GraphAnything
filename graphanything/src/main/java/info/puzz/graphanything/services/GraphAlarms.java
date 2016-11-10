package info.puzz.graphanything.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.activities.BaseActivity;
import info.puzz.graphanything.broadcast.TimerSoundPlayer;
import info.puzz.graphanything.dao.DAO;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;


public final class GraphAlarms {

    private static final String TAG = GraphAlarms.class.getSimpleName();

    public static final String FINAL = "final";
    private static final String GRAPH_ID = "gr_id";

    private GraphAlarms() throws Exception {
        throw new Exception();
    }

    public static void resetAlarms(BaseActivity activity, GraphColumn column) {
        Graph graph = activity.getDAO().loadGraph(column.getGraphId());
        if (!graph.isTimerActive()) {
            Log.i(TAG, "Timer not active");
            return;
        }
        if (graph.isPaused()) {
            Log.i(TAG, "Graph paused");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        if (column.getReminderTimerSound() > 0) {
            int maxTimeMinutes = 60;
            if (column.getFinalTimerSound() > 0) {
                maxTimeMinutes = column.getFinalTimerSound() - 1;
            }

            for (int minutes = column.getReminderTimerSound(); minutes < maxTimeMinutes; minutes += column.getReminderTimerSound()) {
                Intent intent = new Intent(activity, TimerSoundPlayer.class);
                intent.putExtra(FINAL, false);
                intent.putExtra(GRAPH_ID, graph._id);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, minutes, intent, 0);
                alarmManager.cancel(alarmIntent);
                long time = graph.getTimerStarted() + TimeUnit.MINUTES.toMillis(minutes);
                if (time > System.currentTimeMillis()) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, alarmIntent);
                }
            }
        }
        if (column.getFinalTimerSound() > 0) {
            Intent intent = new Intent(activity, TimerSoundPlayer.class);
            intent.putExtra(FINAL, true);
            intent.putExtra(GRAPH_ID, graph._id);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(activity, 27389789, intent, 0);
            alarmManager.cancel(alarmIntent);
            long time = graph.getTimerStarted() + TimeUnit.MINUTES.toMillis(column.getFinalTimerSound());
            if (time > System.currentTimeMillis()) {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, alarmIntent);
            }
        }
    }

    public static void alarm(Context context, Intent intent) {
        DAO dao = new DAO(context);

        boolean isFinal = intent.getBooleanExtra(FINAL, false);
        int graphID = intent.getIntExtra(GRAPH_ID, 0);

        Graph graph = dao.loadGraph(graphID);

        if (!graph.isTimerActive()) {
            Log.i(TAG, "Timer not active");
            return;
        }
        if (graph.isPaused()) {
            Log.i(TAG, "Graph paused");
            return;
        }

        if (isFinal) {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        } else {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
        }
    }
}
