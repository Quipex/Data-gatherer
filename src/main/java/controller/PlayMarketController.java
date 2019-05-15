package controller;

import exceptions.CrawlerException;
import lombok.extern.log4j.Log4j2;
import model.ApplicationInfo;
import persist.LocalCsvStorage;
import utils.Configuration;
import utils.ThreadUtils;
import utils.crawler.PlayMarketCrawler;

import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
public class PlayMarketController extends AbstractScheduledController {
    private static final int HOUR = 60 * 60 * 1000;
    private static final int DAY = 24 * HOUR;
    private static final int SECOND = 1000;
    private static final String PLAY_MARKET_CSV = Configuration.getValue("file.buffer_play_market");
    private List<String> urls;
    private LocalCsvStorage localStorage;

    public PlayMarketController(LocalTime startTime, String url) {
        this(startTime, Arrays.asList(url));
    }

    public PlayMarketController(LocalTime startTime, List<String> urls) {
        super(startTime, DAY);
        this.localStorage = new LocalCsvStorage(Paths.get(PLAY_MARKET_CSV));
        this.urls = urls;
    }

    @Override
    protected void cycle() {
        for (String url : getUrls()) {
            try {
                ApplicationInfo appInfo = PlayMarketCrawler.crawlFromUrl(url);
                log.info("Crawled app info successfully. Url:" + url);
                localStorage.append(Collections.singletonList(appInfo));
                ThreadUtils.sleep(SECOND);
            } catch (CrawlerException e) {
                log.error(e);
            }
        }
    }

    /**
     * @param url to be added
     */
    public void addUrl(String url) {
        urls.add(url);
    }

    /**
     * @return list of urls that are operated on
     */
    public List<String> getUrls() {
        return urls;
    }

    /**
     * @param url to be removed if exists
     * @return true if this list contained the specified element
     */
    public boolean removeUrl(String url) {
        return urls.remove(url);
    }
}
