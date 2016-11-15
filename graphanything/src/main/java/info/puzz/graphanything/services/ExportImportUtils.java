package info.puzz.graphanything.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import info.puzz.graphanything.Constants;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphEntry;
import info.puzz.graphanything.models2.enums.GraphUnitType;
import info.puzz.graphanything.models2.format.FormatException;

/**
 * Created by puzz on 18.04.15..
 */
public class ExportImportUtils {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Constants.LOCALE);

    public static final char DELIMITER = '|';
    public static final String DELIMITER_REGEX = "\\" + DELIMITER;

    public static String exportGraph(Graph graph, List<GraphEntry> entry) throws Exception {
        return null;
//        GraphUnitType graphUnitType = graph.getGraphUnitType();
//        StringBuilder res = new StringBuilder();
//
//        for (GraphEntry value : entry) {
//            if (res.length() > 0) {
//                res.append('\n');
//            }
//
//            res.append(FORMATTER.format(value.created));
//            res.append(DELIMITER);
//            res.append(graphUnitType.format(value.get(0), FormatVariant.LONG));
//        }
//
//        return res.toString();
    }

    public static List<GraphEntry> importGraph(Graph graph, String data, GraphUnitType graphUnitType) throws FormatException {
        List<GraphEntry> res = new ArrayList<GraphEntry>();
        String[] lines = data.split("\n");
        for (String line : lines) {
            String[] parts = line.split(DELIMITER_REGEX);
            if (parts.length == 2) {
                GraphEntry val = new GraphEntry();
                val.graphId = graph._id;
                try {
                    val.created = FORMATTER.parse(parts[0].trim()).getTime();
                } catch (ParseException e) {
                    throw new FormatException("Invalid timestamp:" + parts[0]);
                }
                val.set(0, graphUnitType.parse(parts[1].trim()));
                res.add(val);
            }
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        Graph graph = new Graph();
        graph._id = 7L;
        List<GraphEntry> entries = importGraph(graph, "2016-8-14T9:27:11|\t95.1\n" +
                "2016-8-22T8:40:45|\t92.1\n" +
                "2016-8-31T8:30:12|\t91.1\n" +
                "2016-9-2T9:55:05|\t90.2\n" +
                "2016-9-5T9:24:08|\t91.1\n" +
                "2016-9-7T11:16:06|\t90\n" +
                "2016-9-8T7:58:01|\t89.1\n" +
                "2016-9-9T8:31:26|\t89.1\n" +
                "2016-9-10T7:46:17|\t88.8\n" +
                "2016-9-12T10:46:49|\t90\n" +
                "2016-9-14T8:54:21|\t89\n" +
                "2016-9-15T9:37:12|\t87.9\n" +
                "2016-9-17T10:04:24|\t88.4\n" +
                "2016-9-18T9:39:29|\t89.9\n" +
                "2016-9-20T13:01:02|\t89.6\n" +
                "2016-9-21T9:47:54|\t88.4\n" +
                "2016-9-22T7:42:43|\t88\n" +
                "2016-9-23T7:29:27|\t87.5\n" +
                "2016-9-24T9:07:50|\t87.3\n" +
                "2016-9-26T8:56:16|\t88.4\n" +
                "2016-9-28T9:15:57|\t88.1\n" +
                "2016-9-29T10:25:58|\t88.5\n" +
                "2016-10-1T9:03:16|\t87.8\n" +
                "2016-10-2T9:22:08|\t88\n" +
                "2016-10-3T10:26:52|\t85.6\n" +
                "2016-10-5T8:57:07|\t87.6\n" +
                "2016-10-6T8:01:01|\t87.2\n" +
                "2016-10-7T6:46:40|\t86.7\n", GraphUnitType.UNIT);
        for (GraphEntry entry : entries) {
            //System.out.println((new Timestamp(entry.created) + "->" + entry.value));
        }
        //System.out.println(new SimpleDateFormat("yyyy-MM-dd").parse("2016-8-22").toString());
        //System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2016-8-22T8:40:45").toString());
    }

}
