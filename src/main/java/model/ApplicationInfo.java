package model;

import exceptions.PersistenceException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import utils.Config;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ApplicationInfo implements CSVable {
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

    public void setName(String name) {
        name = name.replace("&amp;", "&");
        name = name.replace(';', ' ');
        this.name = name;
    }

    public String getName() {
        if (name == null || name.equals(""))
            return "<no_name>";
        else
            return name;
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
        return new String[] {
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
}
