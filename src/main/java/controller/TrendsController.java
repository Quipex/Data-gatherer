package controller;

import lombok.extern.log4j.Log4j2;
import model.TrendInfo;
import persist.LocalCsvStorage;
import utils.Configuration;
import utils.ThreadUtils;
import utils.crawler.TrendsCrawler;

import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class TrendsController extends AbstractScheduledController {
    private static final int SECOND = 1000;
    private static final int HOUR = 60 * 60 * SECOND;
    private static final int DAY = 24 * HOUR;
    private static final String TRENDS_CSV = Configuration.getValue("file.buffer_google_trends");
    private List<String> searchStrings;
    private LocalCsvStorage localStorage;
    private TrendsCrawler crawler;

    public TrendsController(LocalTime startTime, String searchString) {
        this(startTime, Arrays.asList(searchString));
    }

    public TrendsController(LocalTime startTime, List<String> searchStrings) {
        super(startTime, DAY);
        this.localStorage = new LocalCsvStorage(Paths.get(TRENDS_CSV));
        this.searchStrings = searchStrings;
        this.crawler = new TrendsCrawler.Builder().build();
    }

    @Override
    protected void cycle() {
        for (String searchString : searchStrings) {
            if (!crawler.getSearchString().equals(searchString)) {
                crawler.setSearchString(searchString);
            }
            TrendInfo trendsInfo = crawler.crawl();
            log.info("Crawled successfully, search: " + searchString);
            localStorage.append(Arrays.asList(trendsInfo));
            ThreadUtils.sleep(SECOND);
        }
    }

    /**
     * @param searchString to be added
     */
    public void addSearchString(String searchString) {
        searchStrings.add(searchString);
    }

    /**
     * @return list of urls that are operated on
     */
    public List<String> getSearchStrings() {
        return searchStrings;
    }

    /**
     * @param searchString to be removed if exists
     * @return true if this list contained the specified element
     */
    public boolean removeSearchString(String searchString) {
        return searchStrings.remove(searchString);
    }
}
