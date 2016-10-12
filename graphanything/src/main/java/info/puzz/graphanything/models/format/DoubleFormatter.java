package info.puzz.graphanything.models.format;

import info.puzz.graphanything.utils.StringUtils;

/**
 * Created by puzz on 11/10/16.
 */

public class DoubleFormatter implements FormatterParser {
    @Override
    public Double parse(String str) throws FormatException {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            throw new FormatException("Invalid number:" + str);
        }
    }

    @Override
    public String format(Double value, boolean shortFormat) {
        if (value == null) {
            return "";
        }
        return String.format(shortFormat ? "%.1f" : "%.2f", value);
    }

    public static void main(String[] args) {
        System.out.println(new DoubleFormatter().format(2.3D, true));
        System.out.println(new DoubleFormatter().format(2.3D, false));
    }
}
