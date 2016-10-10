package info.puzz.graphanything.models;

import com.jjoe64.graphview.series.DataPoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.puzz.graphanything.utils.TimeUtils;
import lombok.Getter;
import nl.qbusict.cupboard.QueryResultIterable;

/**
 * Created by puzz on 06/10/16.
 */

public enum GraphType {
    VALUES(1, "Simple values graph", new ValuesToGraphPointsConverter() {
        @Override
        public List<DataPoint> convert(List<GraphValue> graphValues) {
            ArrayList<DataPoint> res = new ArrayList<>();

            for (GraphValue graphValue : graphValues) {
                res.add(new DataPoint(graphValue.created, graphValue.value));
            }

            return res;
        }
    }),
    SUM_ALL_PREVIOUS(2, "Every value adds up to the previous value", new ValuesToGraphPointsConverter() {
        @Override
        public List<DataPoint> convert(List<GraphValue> graphValues) {
            ArrayList<DataPoint> res = new ArrayList<>();

            double sum = 0D;
            for (GraphValue graphValue : graphValues) {
                sum += graphValue.value;
                res.add(new DataPoint(graphValue.created, sum));
            }

            return res;
        }
    }),
    SUM_DAILY(3, "Group all values on a single day", new ValuesToGraphPointsConverter() {
        @Override
        public List<DataPoint> convert(List<GraphValue> graphValues) {
            ArrayList<DataPoint> res = new ArrayList<>();

            DataPoint dataPoint = null;
            String previousDate = "ignore_this";
            for (GraphValue graphValue : graphValues) {
                Timestamp currTs = new Timestamp(graphValue.created);
                String date = TimeUtils.YYYYMMDD_FORMATTER.format(currTs);
                if (date.equals(previousDate)) {
                    dataPoint = new DataPoint(dataPoint.getX(), dataPoint.getY() + graphValue.value);
                } else {
                    if (dataPoint != null) {
                        res.add(dataPoint);
                    }

                    // New dataPoint for new date:
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currTs);
                    cal.set(Calendar.HOUR_OF_DAY, 12);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);

                    dataPoint = new DataPoint(cal.getTime().getTime(), graphValue.value);
                }
                previousDate = date;
            }
            res.add(dataPoint);

            return res;
        }
    }),
    ;

    @Getter
    private final int type;

    @Getter
    private final String description;

    @Getter
    private final ValuesToGraphPointsConverter converter;

    GraphType(int type, String description, ValuesToGraphPointsConverter converter) {
        this.type = type;
        this.description = description;
        this.converter = converter;
    }

}
