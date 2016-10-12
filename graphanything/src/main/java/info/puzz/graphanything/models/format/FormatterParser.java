package info.puzz.graphanything.models.format;

/**
 * Created by puzz on 11/10/16.
 */

public interface FormatterParser {
    Double parse(String str) throws FormatException;
    String format(Double value, boolean shortFormat);
}
