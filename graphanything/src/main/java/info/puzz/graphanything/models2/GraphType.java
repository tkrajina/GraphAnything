package info.puzz.graphanything.models2;

import com.jjoe64.graphview.series.DataPoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.puzz.graphanything.utils.TimeUtils;
import lombok.Getter;

/**
 * Created by puzz on 06/10/16.
 */

public enum GraphType {
    VALUES(1, "Simple values graph", new ValuesToGraphPointsConverter() {
        @Override
        public List<DataPoint> convert(List<GraphEntry> entries, int columnNo) {
            ArrayList<DataPoint> res = new ArrayList<>();

            for (GraphEntry entry : entries) {
                Double value = entry.get(columnNo);
                res.add(new DataPoint(entry.created, value == null ? 0D : value.doubleValue()));
            }

            return res;
        }
    }),
    SUM_ALL_PREVIOUS(2, "Every value adds up to the previous value", new ValuesToGraphPointsConverter() {
        @Override
        public List<DataPoint> convert(List<GraphEntry> entries, int columnNo) {
            ArrayList<DataPoint> res = new ArrayList<>();

            double sum = 0D;
            for (GraphEntry entry : entries) {
                Double val = entry.get(columnNo);
                sum += val == null ? 0 : val.doubleValue();
                res.add(new DataPoint(entry.created, sum));
            }

            return res;
        }
    }),
    SUM_DAILY(3, "Group all values on a single day", new ValuesToGraphPointsConverter() {
        @Override
        public List<DataPoint> convert(List<GraphEntry> entries, int columnNo) {
            ArrayList<DataPoint> res = new ArrayList<>();

            DataPoint dataPoint = null;
            String previousDate = "ignore_this";
            for (GraphEntry entry : entries) {
                Timestamp currTs = new Timestamp(entry.created);
                String date = TimeUtils.YYYYMMDD_FORMATTER.format(currTs);
                if (date.equals(previousDate)) {
                    dataPoint = new DataPoint(dataPoint.getX(), dataPoint.getY() + entry.get(columnNo));
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

                    dataPoint = new DataPoint(cal.getTime().getTime(), entry.get(columnNo));
                }
                previousDate = date;
            }
            if (dataPoint != null) {
                res.add(dataPoint);
            }

            return res;
        }
    }),
    ;

    @Getter
    private final int type;

    @Getter
    private final String description;

    private final ValuesToGraphPointsConverter converter;

    GraphType(int type, String description, ValuesToGraphPointsConverter converter) {
        this.type = type;
        this.description = description;
        this.converter = converter;
    }

    public List<DataPoint> convert(List<GraphEntry> entries, int columnNo) {
        return converter.convert(entries, columnNo);
    }

    public static GraphType findByType(int type) {
        for (GraphType graphType : values()) {
            if (graphType.getType() == type) {
                return graphType;
            }
        }
        return GraphType.VALUES;
    }
}
