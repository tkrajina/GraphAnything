package info.puzz.graphanything.models2.format;

import info.puzz.graphanything.models2.FormatVariant;

/**
 * Created by puzz on 11/10/16.
 */

public interface FormatterParser {
    Double parse(String str) throws FormatException;
    String format(Double value, FormatVariant variant);
}
