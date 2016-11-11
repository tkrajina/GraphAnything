package info.puzz.graphanything.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.SystemClock;
import android.util.Log;

import junit.framework.Assert;

import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.broadcast.TimerSoundPlayer;
import info.puzz.graphanything.dao.DAO;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.models2.GraphUnitType;


public final class GraphAlarms {

    private static final String TAG = GraphAlarms.class.getSimpleName();

    public static final String FINAL = "final";
    private static final String GRAPH_ID = "gr_id";

    private GraphAlarms() throws Exception {
        throw new Exception();
    }

    public static void resetNextTimerAlarm(Context context, Graph graph) {
        DAO dao = new DAO(context).open();

        GraphColumn column = dao.getColumnsByColumnNo(graph._id).get(0);
        if (!graph.isTimerActive()) {
            Log.i(TAG, "Timer not active");
            return;
        }
        if (graph.isPaused()) {
            Log.i(TAG, "Graph paused");
            return;
        }
        if (column.getGraphUnitType() != GraphUnitType.TIMER) {
            return;
        }

        Log.i(TAG, TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - graph.timerStarted) + " from timer start");
        long elapsedTimeOnTimerStart = SystemClock.elapsedRealtime() - (System.currentTimeMillis() - graph.timerStarted);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (graph.finalTimerSound > 0) {
            long time = elapsedTimeOnTimerStart + TimeUnit.MINUTES.toMillis(graph.finalTimerSound);
            if (time > SystemClock.elapsedRealtime()) {
                Intent finalAlarmIntent = new Intent(context, TimerSoundPlayer.class);
                finalAlarmIntent.putExtra(GRAPH_ID, graph._id);
                finalAlarmIntent.putExtra(FINAL, true);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, finalAlarmIntent, 0);
                alarmMgr.cancel(alarmIntent);
                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, alarmIntent);
            }
        }

        if (graph.reminderTimerSound > 0) {
            for (int minutes = 1; minutes < Math.min(60, graph.finalTimerSound - 1); minutes += graph.reminderTimerSound) {
                long time = elapsedTimeOnTimerStart + TimeUnit.MINUTES.toMillis(minutes);
                if (time > SystemClock.elapsedRealtime()) {
                    Log.i(TAG, String.format("Alarm in %d minutes", TimeUnit.MILLISECONDS.toMinutes(time - SystemClock.elapsedRealtime())));
                    Intent intent = new Intent(context, TimerSoundPlayer.class);
                    intent.putExtra(GRAPH_ID, graph._id);
                    intent.putExtra(FINAL, false);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    alarmMgr.cancel(alarmIntent);
                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, alarmIntent);
                    return;
                }
            }
        }
    }

    public static void alarm(Context context, Intent intent) {
        DAO dao = new DAO(context).open();

        boolean isFinal = intent.getBooleanExtra(FINAL, false);
        Long graphID = intent.getLongExtra(GRAPH_ID, 0);
        Assert.assertNotNull(graphID);
        Assert.assertTrue(graphID.longValue() > 0);

        Graph graph = dao.loadGraph(graphID);
        Assert.assertNotNull(graph);

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
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
        }
        
        resetNextTimerAlarm(context, graph);
    }
}
