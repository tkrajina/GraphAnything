package info.puzz.graphanything.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.Constants;
import info.puzz.graphanything.models2.FormatVariant;

/**
 * Created by puzz on 06/10/16.
 */

public class TimeUtils {

    public static final SimpleDateFormat YYYYMMDDHHMMSS_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Constants.LOCALE);
    public static final SimpleDateFormat YYYYMMDD_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Constants.LOCALE);

    private TimeUtils() throws Exception {
        throw new Exception();
    }

    public static long timeFrom(Long time) {
        if (time == null) {
            return 0;
        }

        return System.currentTimeMillis() - time.longValue();
    }

    public static String formatTimestamp(Long time) {
        if (time == null) {
            return null;
        }
        return YYYYMMDDHHMMSS_FORMATTER.format(new Timestamp(time.longValue()));
    }

    public static String formatTimeAgoString(Long time) {
        if (time == null) {
            return "never";
        }
        if (time.longValue() == 0) {
            return "never";
        }
        return formatTimeDuration(System.currentTimeMillis() - time) + " ago";
    }

    public static String formatDurationToHHMMSS(long duration, FormatVariant variant) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long remainingSeconds = Math.abs(seconds % 60);
        long mins = seconds / 60;
        long remainingMins = Math.abs(mins % 60);
        long hours = mins / 60;
        if (variant == FormatVariant.SHORT) {
            return String.format(Constants.LOCALE, "%02d:%02d", hours, remainingMins);
        }
        return String.format(Constants.LOCALE, "%02d:%02d:%02d", hours, remainingMins, remainingSeconds);
    }

    /** Returng string in format "1 seconds ago" or "12 days ago", depending on timestamp */
    public static String formatTimeDuration(long timeDiff) {
        if (timeDiff < 0) {
            return "n/a";
        }

        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeDiff);
        if (seconds < 60) {
            return seconds + "s";
        }

        int s = seconds % 60;
        int min = (int) (TimeUnit.SECONDS.toMinutes(seconds) % 60);
        if (seconds < TimeUnit.HOURS.toSeconds(1)) {
            return min + "min " + s + "s";
        }

        int hours = (int) (TimeUnit.SECONDS.toHours(seconds) % 24);
        if (seconds < TimeUnit.HOURS.toSeconds(24)) {
            return hours + "h " + min + "min " + s + "s";
        }

        int days = (int) TimeUnit.SECONDS.toDays(seconds);
        if (days < 30) {
            return days + "days " + hours + "h";
        }

        if (days < 365) {
            return days + "days";
        }

        int years = (int) (days / 365.25);

        return years + "years " + days + "days";
    }

    public static void main(String[] args) {
        System.out.println(formatDurationToHHMMSS(61000, FormatVariant.SHORT));
        System.out.println(formatDurationToHHMMSS(61000, FormatVariant.LONG));
    }
}
