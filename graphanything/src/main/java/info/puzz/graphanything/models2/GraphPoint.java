package info.puzz.graphanything.models2;

/**
 * Created by puzz on 07/10/16.
 */

public class GraphPoint {
    long time;
    double value;

    public GraphPoint(long created, double value) {
        this.time = created;
        this.value = value;
    }
}
