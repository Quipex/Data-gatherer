package utils.crawler;

import exceptions.CrawlerException;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Log4j2
abstract class AbstractHtmlCrawler {

    private static final String USER_AGENT = Config.getValue("crawler.user_agent");
    private static final String LATEST_HTML_FILENAME = Config.getValue("file.latest_parsed_html");
    private static final String LATEST_HTML_PATH = Objects.requireNonNull(AbstractHtmlCrawler.class.getClassLoader()
            .getResource(LATEST_HTML_FILENAME)).getPath();

    static void writeToFile(String html) {
        try (FileWriter fw = new FileWriter(LATEST_HTML_PATH)) {
            fw.write(html);
            fw.flush();
        } catch (IOException e) {
            log.error("Can't write to file", e);
        }
    }

    static Document loadFromUrl(String url) {
        Map<String, String> receivedHeaders = null;
        try {
            log.info("Connecting to " + url);
            Connection.Response resp = Jsoup.connect(url)
                    .userAgent(USER_AGENT).execute();
            receivedHeaders = resp.headers();
            Document doc = resp.parse();
            log.info("Connected!");
            return doc;
        } catch (HttpStatusException e) {
            log.error(e);
            if (receivedHeaders != null) {
                log.error("response headers:");
                StringBuilder sb = new StringBuilder();
                for (String s : receivedHeaders.keySet()) {
                    sb.append(s).append(":").append(receivedHeaders.get(s)).append("\n");
                }
                log.error(sb.toString());
            }
            throw new CrawlerException(e);
        } catch (IOException e) {
            log.error("Can't get " + url, e);
            throw new CrawlerException(e);
        }
    }
}
