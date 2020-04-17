package ru.driverdocs.rxrepositories.data;

import ru.driverdocs.domain.DriverLicense;

import java.time.LocalDate;

public final class DriverLicenseImpl implements DriverLicense {
    private final long id;
    private final String series;
    private final String number;
    private final LocalDate startdate;
    private final LocalDate enddate;
    private DriverLicenseImpl(Builder builder) {
        id = builder.id;
        series = builder.series;
        number = builder.number;
        startdate = builder.startdate;
        enddate = builder.enddate;
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

    @Override
    public LocalDate getEnddate() {
        return enddate;
    }

    public static class Builder {
        private long id;
        private String series;
        private String number;
        private LocalDate startdate;
        private LocalDate enddate;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setSeries(String series) {
            this.series = series;
            return this;
        }

        public Builder setNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder setStartdate(LocalDate startdate) {
            this.startdate = startdate;
            return this;
        }

        public Builder setEnddate(LocalDate enddate) {
            this.enddate = enddate;
            return this;
        }

        public DriverLicenseImpl build() {

            if (id <= 0)
                throw new IllegalArgumentException("id не может принимать значения меньше единицы");
            if (series == null || series.trim().isEmpty())
                throw new IllegalArgumentException("series не может отсутствовать");
            if (number == null || number.trim().isEmpty())
                throw new IllegalArgumentException("number не может отсутствовать");
            if (startdate == null)
                throw new IllegalArgumentException("startdate не может отсутствовать");
            if (enddate == null)
                throw new IllegalArgumentException("enddate не может отсутствовать");

            return new DriverLicenseImpl(this);
        }
    }
}
