package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

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

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, i, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                assert titleElement != null;
                Element linkElement = titleElement.child(0);
                Element timeElement = row.select(".vacancy-card__date").first();
                String vacancyName = titleElement.text();
                assert timeElement != null;
                String timeElements = timeElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                try {
                    System.out.printf("%s %s %s %s%n", vacancyName, link, retrieveDescription(link), timeElements);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
