package info.puzz.graphanything.models2.format;

import info.puzz.graphanything.Constants;
import info.puzz.graphanything.models2.FormatVariant;
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
    public String format(Double value, FormatVariant variant) {
        if (value == null) {
            return "n/a";
        }
        return String.format(Constants.LOCALE, variant == FormatVariant.SHORT ? "%.1f" : "%.2f", value);
    }

    public static void main(String[] args) {
        System.out.println(new DoubleFormatter().format(2.3D, FormatVariant.SHORT));
        System.out.println(new DoubleFormatter().format(2.3D, FormatVariant.LONG));
    }
}
