package persist;

import model.ApplicationInfo;
import model.CSVable;
import model.TrendInfo;
import org.apache.commons.csv.CSVRecord;

import java.util.Map;
import java.util.Set;

public class CsvableFactory {

    /**
     * Returns filled CSVRecord, if first header element is equal to those in CSVable POJOs
     *
     * @param record    with csv data
     * @param headerMap contains header names opposite to their position
     * @return filled CSVRecord, if first header element is equal to those in CSVable POJOs, otherwise null
     */
    public static CSVable fromRecord(CSVRecord record, Map<String, Integer> headerMap) {
        CSVable filledEntity;

        final Set<Map.Entry<String, Integer>> headerEntrySet = headerMap.entrySet();
        if (headerEntrySet.size() > 15) {
            filledEntity = fillFromRecord(new TrendInfo(), record);
        } else {
            filledEntity = fillFromRecord(new ApplicationInfo(), record);
        }
        return filledEntity;
    }

    private static CSVable fillFromRecord(CSVable csVable, CSVRecord record) {
        String[] values = new String[record.size()];
        int index = 0;
        for (String value : record) {
            values[index] = value;
            index++;
        }
        csVable.fillFromCsv(values);
        return csVable;
    }
}
