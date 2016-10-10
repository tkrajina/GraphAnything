package info.puzz.graphanything.models;

import com.jjoe64.graphview.series.DataPoint;

import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

/**
 * Created by puzz on 07/10/16.
 */

public interface ValuesToGraphPointsConverter {
    List<DataPoint> convert(List<GraphValue> graphValues);
}
