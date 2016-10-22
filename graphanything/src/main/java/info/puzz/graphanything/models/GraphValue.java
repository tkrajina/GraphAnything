package info.puzz.graphanything.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @deprecated
 * @see GraphEntry
 */
@NoArgsConstructor
@Data
class _GraphValue {

    public Long _id;
    public long graphId;
    public long created;
    public double value;

    public _GraphValue(long created, double value) {
        this.created = created;
        this.value = value;
    }

}
