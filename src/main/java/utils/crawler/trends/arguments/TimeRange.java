package utils.crawler.trends.arguments;

public enum TimeRange {
    PAST_1_HOUR("now 1-H"),
    PAST_4_H("now 4-H"),
    PAST_1_DAY("now 1-d"),
    PAST_7_D("now 7-d"),
    PAST_30_D("today 1-m"),
    PAST_90_D("today 3-m"),
    PAST_5_Y("today 5-y"),
    SINCE_2004("all");

    private String name;

    TimeRange(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
