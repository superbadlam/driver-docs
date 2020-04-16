package ru.driverdocs.ui.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedicalRefValidator {
    private static final Pattern SERIES_PATTERN = Pattern.compile("[0-9]{4}");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]{10})");

    public static boolean isValidSeries(String series) {
        Matcher matcher = SERIES_PATTERN.matcher(series);
        return matcher.matches();
    }

    public static boolean isValidNumber(String number) {
        Matcher matcher = NUMBER_PATTERN.matcher(number);
        return matcher.matches();
    }

    public static boolean isValidDateRange(LocalDate startdate) {
        return startdate.isBefore(LocalDate.now().plusDays(1)) // начало не может быть больше текущей даты
                && ChronoUnit.MONTHS.between(startdate, LocalDate.now()) <= 12;
    }
}
