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
import java.util.Objects;

@Data
public class SummarizedTrendInfo implements CSVable, Dateable, Comparable<SummarizedTrendInfo> {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            Config.getValue("time.datetime_format")
    );
    private String search;
    private LocalDateTime timestamp;
    private int trendSum;
    public SummarizedTrendInfo(TrendInfo trend) {
        check(trend);
        search = trend.getSearch();
        timestamp = extractDateTime(trend);
        trendSum = extractSum(trend);
    }

    public SummarizedTrendInfo() {
    }

    @Override
    public String toString() {
        return "SummarizedTrendInfo{" +
                "search='" + search + '\'' +
                ", timestamp=" + timestamp +
                ", trendSum=" + trendSum +
                '}';
    }

    private void check(TrendInfo trend) {
        if (trend.getSearch() == null || "".equals(trend.getSearch())) {
            throw new PersistenceException("Trend's search string is invalid");
        }
        if (trend.getValues().size() == 0) {
            throw new PersistenceException("Trend's date to value map is empty");
        }
    }

    private LocalDateTime extractDateTime(TrendInfo trend) {
        LocalDateTime oldest = LocalDateTime.of(1970, 1, 1, 0, 0);
        for (LocalDateTime localDateTime : trend.getValues().keySet()) {
            if (oldest.isBefore(localDateTime)) {
                oldest = localDateTime;
            }
        }
        return oldest;
    }

    private int extractSum(TrendInfo trend) {
        int sum = 0;
        for (Map.Entry<LocalDateTime, String> dateToVal : trend.getValues().entrySet()) {
            sum += Integer.parseInt(dateToVal.getValue());
        }
        return sum;
    }

    @Override
    public String[] toCsv() {
        return new String[]{
                getSearch(),
                getTimestamp().format(dateTimeFormatter),
                String.valueOf(getTrendSum())
        };
    }

    @Override
    public void fillFromCsv(String... params) {

    }

    @Override
    public String[] getCsvHeader() {
        return new String[]{
                "SEARCH_STRING",
                "DATETIME",
                "TREND_SUM"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummarizedTrendInfo that = (SummarizedTrendInfo) o;
        return trendSum == that.trendSum &&
                Objects.equals(search, that.search) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(search, timestamp, trendSum);
    }

    @Override
    public int compareTo(SummarizedTrendInfo that) {
        int searchComparison = this.getSearch().compareTo(that.getSearch());
        if (searchComparison != 0)
            return searchComparison;

        return this.getTimestamp().compareTo(that.getTimestamp());
    }
}
