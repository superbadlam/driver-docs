package ru.driverdocs.ui.validator;

import java.time.LocalDate;

public class EmployerLicenseValidator {

    public boolean isValidSeries(String series) {
        return series != null
                && !series.isEmpty();
    }

    public boolean isValidNumber(String number) {
        return number != null
                && !number.isEmpty();
    }

    public boolean isValidDate(LocalDate startdate) {
        return startdate != null;
    }

    public boolean isValid(String number, String series, LocalDate startdate) {
        return isValidNumber(number)
                && isValidSeries(series)
                && isValidDate(startdate);
    }
}
