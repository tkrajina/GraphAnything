package info.puzz.graphanything.models2;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.R;
import info.puzz.graphanything.models2.enums.GraphUnitType;
import info.puzz.graphanything.utils.TimeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by puzz on 24.12.14..
 */
@Data
@Accessors(chain = true)
public class Graph implements Serializable {

    public static final int DEFAULT_STATS_SAMPLE_DAYS = 7;

    public Long _id;
    public String name;

    public double lastValue;
    public long lastValueCreated;
    /**
     * Used when this graph timer is active.
     */
    public long timerStarted;
    /**
     * If <code>&gt;0</code> then it's paused.
     * @see #isPaused()
     */
    public long timerPaused;

    /**
     * Used for statistics (calculate current and last sample days) and goal calculation.
     */
    public int statsPeriod = DEFAULT_STATS_SAMPLE_DAYS;

    /**
     * If {@link GraphColumn#unitType} is {@link GraphUnitType#TIMER} and this field is <code>>0</code> then
     * a beep sound will be played every this amount of minutes until {@link #finalTimerSound}.
     */
    public int reminderTimerSound;

    /**
     * @see #reminderTimerSound
     */
    public int finalTimerSound;

    @Override
    public String toString() {
        return name;
    }

    public boolean isPaused() {
        return timerStarted > 0 && timerPaused > timerStarted;
    }

    /**
     * Keep in mind that it can be paused.
     */
    public boolean isTimerActive() {
        return timerStarted > 0;
    }

    public static void main(String[] args) {
        System.out.println(String.format("%.2f", 2.3D));
    }

    public int getActivityIcon(GraphColumn column) {
        if (column == null) {
            return R.drawable.ic_smile;
        }

        if (column.getGraphUnitType() == GraphUnitType.TIMER && timerStarted > 0) {
            return R.drawable.ic_timer;
        } else if (TimeUtils.timeFrom(lastValueCreated) > TimeUnit.DAYS.toMillis(Graph.DEFAULT_STATS_SAMPLE_DAYS / 2)) {
            return R.drawable.ic_zzz_bell;
        } else if (column.calculateGoal()) {
            if (- Graph.DEFAULT_STATS_SAMPLE_DAYS / 2 < column.goalEstimateDays && column.goalEstimateDays < Graph.DEFAULT_STATS_SAMPLE_DAYS * 50) {
                return R.drawable.ic_smile;
            } else {
                return R.drawable.ic_sad;
            }
        }

        return R.drawable.ic_smile;
    }
}
