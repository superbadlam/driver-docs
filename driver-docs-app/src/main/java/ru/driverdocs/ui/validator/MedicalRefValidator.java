package ru.driverdocs.ui.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class MedicalRefValidator {
    private static final Pattern SERIES_PATTERN = Pattern.compile("[0-9]{4}");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]{10})");

    public boolean isValidSeries(String series) {
        return series != null
                && SERIES_PATTERN.matcher(series).matches();
    }

    public boolean isValidNumber(String number) {
        return number != null
                && NUMBER_PATTERN.matcher(number).matches();
    }

    public boolean isValidDateRange(LocalDate startdate) {
        return startdate != null
                && startdate.isBefore(LocalDate.now().plusDays(1)) // начало не может быть больше текущей даты
                && ChronoUnit.MONTHS.between(startdate, LocalDate.now()) <= 12;
    }

    public boolean isValid(String number, String series, LocalDate startdate) {
        return isValidNumber(number)
                && isValidSeries(series)
                && isValidDateRange(startdate);
    }
}
