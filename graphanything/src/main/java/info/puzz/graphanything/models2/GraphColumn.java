package info.puzz.graphanything.models2;

import java.io.Serializable;

import info.puzz.graphanything.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by puzz on 14/10/16.
 */

@Data
@Accessors(chain = true)
public class GraphColumn implements Serializable {

    public static final int COLUMNS_NO = GraphEntry.COLUMNS_NO;

    public Long _id;
    public long graphId;

    public String name;

    public int columnNo;

    public Double goal;

    /**
     * Number of days to reach goal. It is calculated by comparing the average in the current
     * and previous interval ({@link GraphInfo#statsPeriod} days).
     *
     * If it's negative then the value is diverging from the goal.
     */
    public float goalEstimateDays;

    public int unitType = GraphUnitType.UNIT.getType();

    public String unit;

    public boolean calculateGoal() {
        return goal != null;
    }

    public GraphUnitType getGraphUnitType() {
        return GraphUnitType.findByType(unitType);
    }

    public String formatName() {
        return getName() + (StringUtils.isEmpty(getUnit()) ? "" : String.format(" [%s]", getUnit()));
    }
}