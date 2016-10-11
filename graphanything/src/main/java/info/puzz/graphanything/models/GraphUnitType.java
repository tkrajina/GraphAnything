package info.puzz.graphanything.models;

/**
 * Created by puzz on 05/10/16.
 */

public enum GraphUnitType {

    UNIT(1, "Any unit"),
    TIMER(2, "Time (with timer)"),

    ;

    private final int type;
    private final String description;

    GraphUnitType(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

}
