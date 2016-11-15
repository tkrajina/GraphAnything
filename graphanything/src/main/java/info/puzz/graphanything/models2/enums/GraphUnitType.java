package info.puzz.graphanything.models2.enums;

import info.puzz.graphanything.models2.FormatVariant;
import info.puzz.graphanything.models2.format.DoubleFormatter;
import info.puzz.graphanything.models2.format.FormatException;
import info.puzz.graphanything.models2.format.FormatterParser;
import info.puzz.graphanything.models2.format.TimeFormatter;
import lombok.Getter;

/**
 * Created by puzz on 05/10/16.
 */
public enum GraphUnitType {

    UNIT(1, "Numeric value", new DoubleFormatter()),
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

    public String format(Double value, FormatVariant variant) {
        return formatterParser.format(value, variant);
    }

    public static GraphUnitType findByType(int unitType) {
        for (GraphUnitType t : values()) {
            if (t.getType() == unitType) {
                return t;
            }
        }
        return GraphUnitType.UNIT;
    }
}
