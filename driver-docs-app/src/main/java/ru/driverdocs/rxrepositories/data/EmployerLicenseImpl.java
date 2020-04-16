package ru.driverdocs.rxrepositories.data;

import ru.driverdocs.domain.EmployerLicense;

import java.time.LocalDate;

public final class EmployerLicenseImpl implements EmployerLicense {
    private LocalDate startdate;
    private String number;
    private String series;
    private long id;

    private EmployerLicenseImpl(long id, String series, String number, LocalDate startdate) {
        this.startdate = startdate;
        this.number = number;
        this.series = series;
        this.id = id;
    }

    public static EmployerLicenseImpl createOf(long id, String series, String number, LocalDate startdate) {
        if (id <= 0)
            throw new IllegalArgumentException("id не может принимать значения меньше единицы");
        if (series == null || series.trim().isEmpty())
            throw new IllegalArgumentException("series не может отсутствовать");
        if (number == null || number.trim().isEmpty())
            throw new IllegalArgumentException("number не может отсутствовать");
        if (startdate == null)
            throw new IllegalArgumentException("startdate не может отсутствовать");

        return new EmployerLicenseImpl(id, series, number, startdate);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getSeries() {
        return series;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public LocalDate getStartdate() {
        return startdate;
    }
}
