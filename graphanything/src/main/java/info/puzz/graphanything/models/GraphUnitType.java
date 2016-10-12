package info.puzz.graphanything.models;

import info.puzz.graphanything.models.format.DoubleFormatter;
import info.puzz.graphanything.models.format.FormatException;
import info.puzz.graphanything.models.format.FormatterParser;
import info.puzz.graphanything.models.format.TimeFormatter;
import lombok.Getter;

/**
 * Created by puzz on 05/10/16.
 */
public enum GraphUnitType {

    UNIT(1, "Any unit", new DoubleFormatter()),
    TIMER(2, "Time (with timer)", new TimeFormatter()),

    ;

    @Getter
    private final int type;
    @Getter
    private final String description;
    private final FormatterParser formatterParser;

    GraphUnitType(int type, String description, FormatterParser formatterParser) {
        this.type = type;
        this.description = description;
        this.formatterParser = formatterParser;
    }

    public Double parse(String str) throws FormatException {
        return formatterParser.parse(str);
    }

    public String format(Double value, boolean shortFormat) {
        return formatterParser.format(value, shortFormat);
    }

}
