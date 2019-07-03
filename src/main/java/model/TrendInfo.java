package model;

import exceptions.PersistenceException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import utils.Config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Data
public class TrendInfo implements CSVable {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            Config.getValue("time.datetime_format")
    );

    private String search;
    private SortedMap<LocalDateTime, String> values;

    @Override
    public String toString() {
        return "TrendInfo{" +
                "search='" + search + '\'' +
                ", values=" + values +
                '}';
    }

    @Override
    public String[] toCsv() {
        int propertiesNum = 1 + values.size();
        String[] copiedValues = new String[propertiesNum];
        copiedValues[0] = search;
        int propertyIndex = 1;
        for (Map.Entry<LocalDateTime, String> timestampToVal : values.entrySet()) {
            String copiedVal = timestampToVal.getKey().format(dateTimeFormatter) + '=' + timestampToVal.getValue();
            copiedValues[propertyIndex] = copiedVal;
            propertyIndex++;
        }
        return copiedValues;
    }

    @Override
    public void fillFromCsv(String... params) {
        setSearch(params[0]);

        final int paramsSize = params.length;
        SortedMap<LocalDateTime, String> copiedMap = new TreeMap<>();
        for (int i = 1; i < paramsSize; i++) {
            String[] paramsParts = params[i].split("=");
            if (paramsParts.length != 2) {
                throw new PersistenceException("Got a parameter that is not represented as date=val, " +
                        "so cannot fill an object");
            }
            LocalDateTime date = LocalDateTime.parse(paramsParts[0], dateTimeFormatter);
            String value = paramsParts[1];
            copiedMap.put(date, value);
        }
        setValues(copiedMap);
    }

    @Override
    public String[] getCsvHeader() {
        final int valuesSize = values.size();
        String[] csvHeader = new String[valuesSize + 1];
        csvHeader[0] = "SEARCH_QUERY";

        for (int i = 1; i <= valuesSize; i++) {
            String headerColumn = "DATE" + i + "=VALUE" + i;
            csvHeader[i] = headerColumn;
        }

        return csvHeader;
    }
}
