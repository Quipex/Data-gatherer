package model;

public class AppAndTrendsInfo extends ApplicationInfo {
    private SummarizedTrendInfo[] trends;
    private String[] csvHeader;

    public AppAndTrendsInfo(ApplicationInfo appInfo, SummarizedTrendInfo... trends) {
        super(appInfo);
        this.trends = trends;
        this.csvHeader = generateHeader(appInfo, trends);
    }

    private String[] generateHeader(ApplicationInfo appInfo, SummarizedTrendInfo... trends) {
        String[] infoHeader = appInfo.getCsvHeader();
        int initialHeadersLength = infoHeader.length;
        String[] headerWithTrends = new String[initialHeadersLength + trends.length];
        System.arraycopy(infoHeader, 0, headerWithTrends, 0, initialHeadersLength);
        int indexAfterInitialHeader = initialHeadersLength;
        for (SummarizedTrendInfo trend : trends) {
            headerWithTrends[indexAfterInitialHeader] = trend.getSearch();
            indexAfterInitialHeader++;
        }
        return headerWithTrends;
    }

    @Override
    public String[] toCsv() {
        String[] initialToCsv = super.toCsv();
        int initialCsvLength = initialToCsv.length;
        String[] toCsvWithTrends = new String[initialCsvLength + trends.length];
        System.arraycopy(initialToCsv, 0, toCsvWithTrends, 0, initialCsvLength);
        int indexOfTrendsPart = initialCsvLength;
        for (SummarizedTrendInfo trend : trends) {
            toCsvWithTrends[indexOfTrendsPart] = String.valueOf(trend.getTrendSum());
            indexOfTrendsPart++;
        }
        return toCsvWithTrends;
    }

    @Override
    public String[] getCsvHeader() {
        return csvHeader;
    }
}
