package model;

import exceptions.PersistenceException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import utils.Config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
@Data
public class ApplicationInfo implements CSVable, Dateable, Comparable<ApplicationInfo> {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            Config.getValue("time.datetime_format")
    );

    //    app name
    private String name;
    //    number of downloads
    private long downloads;
    //    app rating
    private double rating;
    //    number of reviews
    private long reviews;
    //    number of 5* reviews
    private long fiveStars;
    //    number of 4* reviews
    private long fourStars;
    //    number of 3* reviews
    private long threeStars;
    //    number of 2* reviews
    private long twoStars;
    //    number of 1* reviews
    private long oneStars;
    //    date when application info was taken
    private LocalDateTime timestamp;

    public ApplicationInfo() {
    }

    public ApplicationInfo(ApplicationInfo cloned) {
        setName(cloned.getName());
        setDownloads(cloned.getDownloads());
        setRating(cloned.getRating());
        setReviews(cloned.getReviews());
        setFiveStars(cloned.getFiveStars());
        setFourStars(cloned.getFourStars());
        setThreeStars(cloned.getThreeStars());
        setTwoStars(cloned.getTwoStars());
        setOneStars(cloned.getOneStars());
        setTimestamp(cloned.getTimestamp());
    }

    public String getName() {
        if (name == null || name.equals(""))
            return "<no_name>";
        else
            return name;
    }

    public void setName(String name) {
        name = name.replace("&amp;", "&");
        name = name.replace(';', ' ');
        this.name = name;
    }

    @Override
    public String[] toCsv() {
        return new String[]{
                timestamp.format(dateTimeFormatter),
                getName(),
                String.valueOf(downloads),
                String.valueOf(rating),
                String.valueOf(reviews),
                String.valueOf(fiveStars),
                String.valueOf(fourStars),
                String.valueOf(threeStars),
                String.valueOf(twoStars),
                String.valueOf(oneStars)
        };
    }

    @Override
    public void fillFromCsv(String... params) {
        if (params.length != 10) {
            throw new PersistenceException("Parameters number is not 10. Can not fill the object.");
        }
        setTimestamp(LocalDateTime.parse(params[0], dateTimeFormatter));
        setName(params[1]);
        setDownloads(Long.parseLong(params[2]));
        setRating(Double.parseDouble(params[3]));
        setReviews(Long.parseLong(params[4]));
        setFiveStars(Long.parseLong(params[5]));
        setFourStars(Long.parseLong(params[6]));
        setThreeStars(Long.parseLong(params[7]));
        setTwoStars(Long.parseLong(params[8]));
        setOneStars(Long.parseLong(params[9]));
    }

    @Override
    public String[] getCsvHeader() {
        return new String[]{
                "TIMESTAMP",
                "NAME",
                "DOWNLOADS",
                "RATING",
                "REVIEWS",
                "FIVE_STARS",
                "FOUR_STARS",
                "THREE_STARS",
                "TWO_STARS",
                "ONE_STARS"
        };
    }

    @Override
    public int compareTo(ApplicationInfo o) {
        int names = this.getName().compareTo(o.getName());
        if (names != 0) {
            return names;
        } else {
            return this.getTimestamp().compareTo(o.getTimestamp());
        }
    }

    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }

    public void setRating(double rating) {
        if (rating < 1 || rating > 5) {
            log.warn("Rating out of bounds: " + rating);
        }
        this.rating = rating;
    }

    public void setReviews(long reviews) {
        if (reviews < 1) {
            log.warn("reviews out of bounds: " + reviews);
            reviews = 1;
        }
        this.reviews = reviews;
    }

    public void setFiveStars(long fiveStars) {
        if (fiveStars < 0) {
            log.warn("fives out of bounds: " + fiveStars);
            fiveStars = 0;
        }
        this.fiveStars = fiveStars;
    }

    public void setFourStars(long fourStars) {
        if (fourStars < 0) {
            log.warn("fours out of bounds: " + fourStars);
            fourStars = 0;
        }
        this.fourStars = fourStars;
    }

    public void setThreeStars(long threeStars) {
        if (threeStars < 0) {
            log.warn("threes out of bounds: " + threeStars);
            threeStars = 0;
        }
        this.threeStars = threeStars;
    }

    public void setTwoStars(long twoStars) {
        if (twoStars < 0) {
            log.warn("twos out of bounds: " + twoStars);
            twoStars = 0;
        }
        this.twoStars = twoStars;
    }

    public void setOneStars(long oneStars) {
        if (oneStars < 0) {
            log.warn("ones out of bounds: " + oneStars);
            oneStars = 0;
        }
        this.oneStars = oneStars;
    }
}
