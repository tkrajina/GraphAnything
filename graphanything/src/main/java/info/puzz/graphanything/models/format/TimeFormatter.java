package info.puzz.graphanything.models.format;

import java.util.concurrent.TimeUnit;

import info.puzz.graphanything.models.FormatVariant;
import info.puzz.graphanything.utils.StringUtils;
import info.puzz.graphanything.utils.TimeUtils;

/**
 * Created by puzz on 11/10/16.
 */

public class TimeFormatter implements FormatterParser {
    @Override
    public Double parse(String str) throws FormatException {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        int hours, minutes, seconds = 0;

        String[] parts = str.split(":");
        if (parts.length < 2 || parts.length > 3) {
            throw new FormatException("Invalid time:" + str);
        }
        try {

            hours = Integer.parseInt(parts[0].trim());
            minutes = Integer.parseInt(parts[1].trim());
            if (parts.length == 3) {
                seconds = Integer.parseInt(parts[2].trim());
            }
        } catch (NumberFormatException e) {
            throw new FormatException("Invalid time:" + str);
        }

        return Double.valueOf(TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds));
    }

    @Override
    public String format(Double value, FormatVariant variant) {
        if (value == null) {
            return "n/a";
        }
        return TimeUtils.formatDurationToHHMMSS(value.longValue(), variant);
    }

}
