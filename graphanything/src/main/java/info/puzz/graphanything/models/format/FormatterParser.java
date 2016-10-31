package info.puzz.graphanything.models.format;


import info.puzz.graphanything.models2.FormatVariant;

public interface FormatterParser {
    Double parse(String str) throws FormatException;
    String format(Double value, FormatVariant variant);
}
