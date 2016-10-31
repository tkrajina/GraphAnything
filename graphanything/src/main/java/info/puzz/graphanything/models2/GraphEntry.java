package info.puzz.graphanything.models2;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class GraphEntry implements Serializable {

    public static final int COLUMNS_NO = 5;

    public Long _id;
    public long graphId;
    public long created;

    public String comment;

    public Double value0;
    public Double value1;
    public Double value2;
    public Double value3;
    public Double value4;
    /*
    public Double value5;
    public Double value6;
    public Double value7;
    public Double value8;
    public Double value9;
    */

    String message;

    /**
     * Stupid, but it works.
     */
    public GraphEntry set(int i, Double val) {
        if (i < 0 || i > COLUMNS_NO) {
            throw new Error("Invalid index:" + i);
        }
        switch (i) {
            case 0: value0 = val; break;
            case 1: value1 = val; break;
            case 2: value2 = val; break;
            case 3: value3 = val; break;
            case 4: value4 = val; break;
            /*
            case 5: value5 = val;
            case 6: value6 = val;
            case 7: value7 = val;
            case 8: value8 = val;
            case 9: value9 = val;
            */
        }
        return this;
    }

    public Double get(int i) {
        switch (i) {
            case 0: return value0;
            case 1: return value1;
            case 2: return value2;
            case 3: return value3;
            case 4: return value4;
            /*
            case 5: return value5;
            case 6: return value6;
            case 7: return value7;
            case 8: return value8;
            case 9: return value9;
            */
            default: throw new Error("Invalid index:" + i);
        }
    }

}
