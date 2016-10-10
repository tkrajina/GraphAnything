package info.puzz.graphanything.models;

/**
 * Created by puzz on 05/10/16.
 */

public enum GraphUnitType {
    UNIT(1, "Any unit", null),
    TIMER(2, "Time (with timer)", "s"),;

    private final int type;
    private final String description;
    private final String unit;

    GraphUnitType(int type, String description, String unit) {
        this.type = type;
        this.description = description;
        this.unit = unit;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }
}
