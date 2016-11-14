package info.puzz.graphanything.utils;

import info.puzz.graphanything.models.format.FormatException;

/**
 * Created by puzz on 14/11/2016.
 */
public class ParserUtils {
    public static int parseInteger(String value, int defaultValue) throws FormatException {
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new FormatException("Invalid number:" + value);
        }
    }
}
