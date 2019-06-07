package utils.crawler;

import exceptions.CrawlerException;
import lombok.extern.log4j.Log4j2;
import model.ApplicationInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class PlayMarketCrawler extends AbstractHtmlCrawler {

    private PlayMarketCrawler() {}

    public static ApplicationInfo crawlFromUrl(String url) {
        Document doc = loadFromUrl(url);
        writeToFile(doc.html());

        Element body = doc.body();

        ApplicationInfo appInfo = processAppInfo(body);
        log.debug("Processed app info: " + appInfo);
        return appInfo;
    }

    private static ApplicationInfo processAppInfo(Element body) {
        ApplicationInfo appInfo = new ApplicationInfo();

        String name = parseName(body);
        appInfo.setName(name);

        final long downloads = parseDownloads(body);
        appInfo.setDownloads(downloads);

        final double rating = parseRating(body);
        appInfo.setRating(rating);

        final long reviews = parseReviews(body);
        appInfo.setReviews(reviews);

        long[] reviewsByStars = getReviewsByStars(body, reviews);
        appInfo.setFiveStars(reviewsByStars[0]);
        appInfo.setFourStars(reviewsByStars[1]);
        appInfo.setThreeStars(reviewsByStars[2]);
        appInfo.setTwoStars(reviewsByStars[3]);
        appInfo.setOneStars(reviewsByStars[4]);

        appInfo.setTimestamp(LocalDateTime.now());
        return appInfo;
    }

    private static long[] getReviewsByStars(Element body, long reviews) {
        List<Node> starsSection = parseStarsSection(body);
        int[] starPercents = new int[5];
        int starIndex = 0;
        for (Node starNode : starsSection) {
            int starPercent = parseStarPercent(starNode);
            starPercents[starIndex] = starPercent;
            starIndex++;
        }
        return calcReviewsByPercents(reviews, starPercents);
    }

    private static int parseStarPercent(Node starNode) {
        String regex = "width: ([0-9]+)%";
        String styleWithWidth = starNode.childNode(1).attr("style");
        Matcher m = Pattern.compile(regex).matcher(styleWithWidth);
        if (!m.matches()) {
            throw new CrawlerException("Can't parse width: N% to get star percentage. Probably Play Market source code changed.");
        }
        return Integer.parseInt(m.group(1));
    }

    private static long parseReviews(Element body) {
        final String cssQueryReviews = "meta[itemprop=reviewCount]";
        Elements elementsReview = body.select(cssQueryReviews);
        String reviewCountAsString = elementsReview.attr("content");
        return Long.parseLong(reviewCountAsString);
    }

    private static List<Node> parseStarsSection(Element body) {
        final String cssQueryStars = "div[aria-label^=Rated]";
        Elements elementsStars = body.select(cssQueryStars);
        for (Element starsElem : elementsStars) {
            if (starsElem.hasText()) { //div with text of rating (e.g 4.2)
                return starsElem.parent() //div
                        .parent() //c-wiz
                        .childNode(1) //div with stars
                        .childNodes();
            }
        }
        throw new CrawlerException("Can't find rating div");
    }

    private static double parseRating(Element body) {
        final String cssQueryRating = "meta[itemprop=ratingValue]";
        Elements elementsRating = body.select(cssQueryRating);
        String ratingAsString = elementsRating.get(0).attr("content");
        return Double.parseDouble(ratingAsString);
    }

    private static long parseDownloads(Element body) {
        final String cssQueryFooter = "div:containsOwn(Installs)";
        Elements elementsFooter = body.select(cssQueryFooter);
        String downloadsAsString = elementsFooter.next()
                .get(0)
                .childNode(0)
                .childNode(0)
                .toString();
        return parseLong(downloadsAsString);
    }

    private static String parseName(Element body) {
        final String cssQueryName = "h1[itemprop=name]";
        Elements elementsName = body.select(cssQueryName);
        return elementsName.get(0) //h1
                .childNode(0) //span
                .childNode(0) //text
                .toString();
    }

    private static long parseLong(String downloadsAsString) {
        final String[] downloadStrings = downloadsAsString.split("[^0-9]+");
        StringBuilder downloadsString = new StringBuilder();
        for (String downloadsStringPart : downloadStrings) {
            downloadsString.append(downloadsStringPart);
        }
        String resultingDownloadString = downloadsString.toString();
        return Long.parseLong(resultingDownloadString);
    }

    private static long[] calcReviewsByPercents(long sum, int[] percents) {
        if (percents.length != 5) {
            throw new CrawlerException("Number of percents must be 5. Probably Play market page was modified.");
        }
        float percentSum = 0;
        for (int percent : percents) {
            percentSum += percent / 100.0;
        }
        float hundredPercentVal = sum / percentSum;

        long[] ans = new long[5];
        int i = 0;
        for (long percent : percents) {
            ans[i] = Math.round(hundredPercentVal * (percent / 100.0));
            i++;
        }

        return ans;
    }
}
