package ru.driverdocs.ui.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DriverLicenseValidator {
    private static final Pattern SERIES_PATTERN = Pattern.compile("[0-9][0-9] [0-9][0-9]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]{6})");
    private static final long EXPIRY_YEARS = 10;

    public static boolean isValidSeries(String series) {
        Matcher matcher = SERIES_PATTERN.matcher(series);
        return matcher.matches();
    }

    public static boolean isValidNumber(String number) {
        Matcher matcher = NUMBER_PATTERN.matcher(number);
        return matcher.matches();
    }

    public static boolean isValidDateRange(LocalDate startdate, LocalDate enddate) {
        return enddate.isAfter(startdate) // начало старше конца
                && EXPIRY_YEARS == (ChronoUnit.YEARS.between(startdate, enddate)) // разница ровно 10 лет
                && startdate.isBefore(LocalDate.now().plusDays(1)) // начало не может быть больше текущей даты
                && ChronoUnit.DAYS.between(startdate, LocalDate.now()) <= (ChronoUnit.DAYS.between(startdate, enddate));
    }
}
