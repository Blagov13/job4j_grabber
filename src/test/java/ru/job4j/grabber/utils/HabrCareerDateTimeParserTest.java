package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import ru.job4j.grabber.utils.DateTimeParser;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {
    @Test
    void parse() {
        DateTimeParser parser = new HabrCareerDateTimeParser();
        String textDate = "2023-11-13T11:32:03+03:00";
        LocalDateTime ex = LocalDateTime.of(2023, Month.NOVEMBER, 13, 11, 32, 3);
        assertThat(ex).isEqualTo(parser.parse(textDate));
    }
}