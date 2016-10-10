package info.puzz.graphanything.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by puzz on 22.03.15..
 */
public final class Formatters {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static final int ROUND_DIGITS = 2;
    public static final String NOT_AVAILABLE = "n/a";

    private Formatters() throws Exception {
        throw new Exception();
    }

    public static String formatNumber(Float f) {
        if (f == null) {
            return NOT_AVAILABLE;
        }
        if (Float.isNaN(f) || Float.isInfinite(f)) {
            return NOT_AVAILABLE;
        }

        return String.format("%.2f", f.floatValue());
    }

    public static String formatDouble(Double d) {
        if (d == null) {
            return NOT_AVAILABLE;
        }
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return NOT_AVAILABLE;
        }
        return String.format("%.2f", d.floatValue());
    }

    public static String formatDate(long time) {
        if (time == 0) {
            return "-";
        }
        return TIMESTAMP_FORMAT.format(new Date(time));
    }

    public static String formatValue(double value, String unit) {
        return round(value, ROUND_DIGITS) + (unit == null || unit.length() == 0 ? "" : unit);
    }

    private static double round(double value, int places) {
        double pow = Math.pow(10, places);
        return Math.round(value * pow) / pow;
    }

    public static void main(String[] args) {
        System.out.println(formatDate(System.currentTimeMillis()));
        System.out.println(round(2.33, 2));
        System.out.println(round(2.337, 2));
        System.out.println(round(2.331, 2));
        System.out.println(round(2.999, 2));
    }

}
