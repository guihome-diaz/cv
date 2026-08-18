package eu.daxiongmao.prv.cv.dto.experience;

import java.time.LocalDate;

public record YearMonth(
        int year,
        int month,
        int day,
        LocalDate date
) {

    public YearMonth(int year, int month, int day) {
        this(year, month, day, LocalDate.of(year, month, day));
    }

    public YearMonth(int year, int month, int day, LocalDate date) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.date = (date != null ? date : LocalDate.of(year, month, day));
    }
}
