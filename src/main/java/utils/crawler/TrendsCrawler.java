package utils.crawler;

import exceptions.CrawlerException;
import lombok.extern.log4j.Log4j2;
import model.TrendInfo;
import org.apache.commons.collections.map.UnmodifiableMap;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Configuration;
import utils.crawler.trends.arguments.Region;
import utils.crawler.trends.arguments.TimeRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

@Log4j2
public class TrendsCrawler {
    private static final String GTRENDS_LINK = Configuration.getValue("crawler.g-trends.link");
    private static final String BASE = GTRENDS_LINK + Configuration.getValue("crawler.g-trends.link.explore");
    private static final DateTimeFormatter formatter1Day = get1DayFormatter();
    private Map<String, String> keyToArg;
    private String searchString;
    private WebDriver webDriver;

    private TrendsCrawler(Builder builder) {
        this.keyToArg = builder.keyToArg;
        buildSearchString();
    }

    private static DateTimeFormatter get1DayFormatter() {
        Map<Long, String> monthNameMap = new HashMap<>();
        monthNameMap.put(1L, "янв.");
        monthNameMap.put(2L, "февр.");
        monthNameMap.put(3L, "мар.");
        monthNameMap.put(4L, "апр.");
        monthNameMap.put(5L, "мая");
        monthNameMap.put(6L, "июн.");
        monthNameMap.put(7L, "июл.");
        monthNameMap.put(8L, "авг.");
        monthNameMap.put(9L, "сент.");
        monthNameMap.put(10L, "окт.");
        monthNameMap.put(11L, "нояб.");
        monthNameMap.put(12L, "дек.");
        return new DateTimeFormatterBuilder()
                .appendPattern("d ")
                .appendText(ChronoField.MONTH_OF_YEAR, monthNameMap)
                .appendLiteral(" в")
                .appendPattern(" HH:mm")
                .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
                .toFormatter();
    }

    private static String sanitize(String date) {
        char[] chars = new char[date.length() - 2];
        int i = 0;
        for (char c : date.toCharArray()) {
            if (c == '\u202A' || c == '\u202C') {
                continue;
            }
            chars[i] = c;
            i++;
        }
        return new String(chars);
    }

    private void buildSearchString() {
        searchString = getBuiltUrl();
    }

    private void checkSearchString() {
        if (!keyToArg.containsKey("q")) {
            throw new CrawlerException("No search query");
        }
    }

    private String getBuiltUrl() {
        StringBuilder builder = new StringBuilder(BASE);

        for (Map.Entry<String, String> keyToArg : keyToArg.entrySet()) {
            builder.append(keyToArg.getKey())
                    .append("=")
                    .append(keyToArg.getValue())
                    .append("&");
        }

        return builder.toString();
    }

    /**
     * Instantiate a new webdriver, launch search url and crawl data from it
     *
     * @return crawled data
     * @throws CrawlerException if no search string
     */
    public TrendInfo crawl() {
        checkSearchString();

        webDriver = getWebDriver();
        webDriver.get(searchString);

        WebDriverWait wait = new WebDriverWait(webDriver,5);
        String dataEntryXpath = "//div[@aria-label='A tabular representation of the data in the chart.']//table//tbody//tr";
        List<WebElement> dataEntry = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(dataEntryXpath)));

        SortedMap<LocalDateTime, String> dateToSearch = new TreeMap<>();
        for (WebElement tr : dataEntry) {
            List<WebElement> tds = tr.findElements(By.xpath(".//td"));
            String date = tds.get(0).getAttribute("textContent");
            date = sanitize(date);
            LocalDateTime ldt = LocalDateTime.parse(date, formatter1Day);
            String value = tds.get(1).getAttribute("textContent");
            dateToSearch.put(ldt, value);
        }

        TrendInfo info = new TrendInfo();
        info.setSearch(keyToArg.get("q"));
        info.setValues(dateToSearch);
        return info;
    }

    private WebDriver getWebDriver() {
        if (webDriver == null) {
            ChromeOptions options = new ChromeOptions()
                    .addArguments("--headless")
                    .addArguments("--lang=ru");
            WebDriver driver = new ChromeDriver(options);
            driver.get(GTRENDS_LINK);
            return driver;
        } else {
            return webDriver;
        }

    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getCrawlerParameters() {
        return UnmodifiableMap.decorate(keyToArg);
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        if (searchString == null || searchString.equals("")) {
            return;
        }
        keyToArg.put("q", searchString);
        buildSearchString();
    }

    /**
     * Class that is used to create a query and get data from it
     */
    public static class Builder {

        private final Map<String, String> keyToArg = new HashMap<>();

        /**
         * Uses default region of UA and time range of 1 day past
         */
        public Builder() {
            setDefaultArgs();
        }

        public Builder(String searchString) {
            this();
            setSearchString(searchString);
        }

        private void setDefaultArgs() {
            setRegion(Region.UA);
            setTimeRange(TimeRange.PAST_1_DAY);
        }

        /**
         * @param region to be set, if utils.crawler.trends.arguments.Region.ALL - no region is set (worldwide)
         */
        public Builder setRegion(Region region) {
            if (region == Region.ALL) {
                keyToArg.remove("geo");
                return this;
            }
            keyToArg.put("geo", region.name());
            return this;
        }

        /**
         * @param search a search string to make a search for. If null, or "" - search string arg gets removed
         */
        @SuppressWarnings("WeakerAccess")
        public Builder setSearchString(String search) {
            if (search == null || "".equals(search)) {
                keyToArg.remove("q");
                return this;
            }
            keyToArg.put("q", search);
            return this;
        }

        /**
         * @param range to be set. if null - default value is set (past 1 day)
         */
        @SuppressWarnings("WeakerAccess")
        public Builder setTimeRange(TimeRange range) {
            // TODO: 15.05.2019 implement different time parse format
            if (range == null || range == TimeRange.PAST_1_DAY) {
                range = TimeRange.PAST_1_DAY;
            } else {
                throw new UnsupportedOperationException();
            }
            keyToArg.put("date", range.toString());
            return this;
        }

        /**
         * @return crawler with given parameters
         * @throws CrawlerException if no search query was specified
         */
        public TrendsCrawler build() {
            return new TrendsCrawler(this);
        }
    }
}
