package info.puzz.graphanything.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @deprecated
 * @see GraphEntry
 */
@NoArgsConstructor
@Data
public class GraphValue {

    public Long _id;
    public long graphId;
    public long created;
    public double value;

    public GraphValue(long created, double value) {
        this.created = created;
        this.value = value;
    }

}
