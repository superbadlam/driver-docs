package ru.driverdocs.ui.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class DriverLicenseValidator {
    public static final String SERIES_PATTERN_TEMPLATE = "[0-9][0-9] [0-9][0-9]";
    private static final Pattern SERIES_PATTERN = Pattern.compile(SERIES_PATTERN_TEMPLATE);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]{6})");
    private static final long EXPIRY_YEARS = 10;

    public boolean isValidSeries(String series) {
        return series != null
                && SERIES_PATTERN.matcher(series).matches();
    }

    public boolean isValidNumber(String number) {
        return number != null
                && NUMBER_PATTERN.matcher(number).matches();
    }

    public boolean isValidDateRange(LocalDate startdate, LocalDate enddate) {

        return startdate != null && enddate != null
                && enddate.isAfter(startdate) // начало старше конца
                && EXPIRY_YEARS == (ChronoUnit.YEARS.between(startdate, enddate)) // разница ровно 10 лет
                && startdate.isBefore(LocalDate.now().plusDays(1)) // начало не может быть больше текущей даты
                && ChronoUnit.DAYS.between(startdate, LocalDate.now()) <=
                (ChronoUnit.DAYS.between(startdate, enddate));
    }

    public boolean isValid(String number, String series, LocalDate startdate, LocalDate enddate) {
        return isValidNumber(number)
                && isValidSeries(series)
                && isValidDateRange(startdate, enddate);
    }
}
