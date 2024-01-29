package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        final String[] descriptions = new String[1];
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".faded-content__body");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-description__text").first();
            descriptions[0] = titleElement.text();
        });
        return descriptions[0];
    }

    private Post postParsing(Element row) throws IOException {
        int id = 0;
        Element titleElement = row.select(".vacancy-card__title").first();
        assert titleElement != null;
        Element linkElement = titleElement.child(0);
        Element timeElement = row.select("datetime").first();
        String vacancyName = titleElement.text();
        assert timeElement != null;
        LocalDateTime dateVacancy = dateTimeParser.parse(timeElement.attr("datetime"));
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        return new Post(id, vacancyName, link, retrieveDescription(link), dateVacancy);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> vacancy = new ArrayList<>();
        try {
            for (int i = 1; i <= 5; i++) {
                Connection connection = Jsoup.connect(link);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                for (Element row : rows) {
                    vacancy.add(postParsing(row));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vacancy;
    }
}
