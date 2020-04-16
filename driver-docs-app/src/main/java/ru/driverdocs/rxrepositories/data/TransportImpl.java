package ru.driverdocs.rxrepositories.data;

import ru.driverdocs.domain.Transport;

public final class TransportImpl implements Transport {
    private final long id;
    private final String plateNo;
    private final String marka;
    private final String model;
    private final int seats;
    private final String passportSeries;
    private final String passportNumber;
    private final String certificateSeries;
    private final String certificateNumber;

    public TransportImpl(Builder builder) {
        this.id = builder.id;
        this.plateNo = builder.plateNo;
        this.marka = builder.marka;
        this.model = builder.model;
        this.seats = builder.seats;
        this.passportSeries = builder.passportSeries;
        this.passportNumber = builder.passportNumber;
        this.certificateSeries = builder.certificateSeries;
        this.certificateNumber = builder.certificateNumber;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getPlateNo() {
        return plateNo;
    }

    @Override
    public String getMarka() {
        return marka;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public int getSeats() {
        return seats;
    }

    @Override
    public String getPassportSeries() {
        return passportSeries;
    }

    @Override
    public String getPassportNumber() {
        return passportNumber;
    }

    @Override
    public String getCertificateSeries() {
        return certificateSeries;
    }

    @Override
    public String getCertificateNumber() {
        return certificateNumber;
    }

    public static class Builder {
        private long id;
        private String plateNo;
        private String marka;
        private String model;
        private int seats;
        private String passportSeries;
        private String passportNumber;
        private String certificateSeries;
        private String certificateNumber;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setPlateNo(String plateNo) {
            this.plateNo = plateNo;
            return this;
        }

        public Builder setMarka(String marka) {
            this.marka = marka;
            return this;
        }

        public Builder setModel(String model) {
            this.model = model;
            return this;
        }

        public Builder setSeats(int seats) {
            this.seats = seats;
            return this;
        }

        public Builder setPassportSeries(String passportSeries) {
            this.passportSeries = passportSeries;
            return this;
        }

        public Builder setPassportNumber(String passportNumber) {
            this.passportNumber = passportNumber;
            return this;
        }

        public Builder setCertificateSeries(String certificateSeries) {
            this.certificateSeries = certificateSeries;
            return this;
        }

        public Builder setCertificateNumber(String certificateNumber) {
            this.certificateNumber = certificateNumber;
            return this;
        }

        public TransportImpl build() {
            if (id <= 0)
                throw new IllegalArgumentException("id не может принимать значения меньше единицы");
            if (plateNo == null || plateNo.trim().isEmpty())
                throw new IllegalArgumentException("plateNo не может отсутствовать");
            if (marka == null || marka.trim().isEmpty())
                throw new IllegalArgumentException("marka не может отсутствовать");
            if (model == null || model.trim().isEmpty())
                throw new IllegalArgumentException("model не может отсутствовать");
            if (seats < 0)
                throw new IllegalArgumentException("seats не может принимать значения меньше 0");
            if (passportNumber == null || passportNumber.trim().isEmpty())
                throw new IllegalArgumentException("passportNumber не может отсутствовать");
            if (passportSeries == null || passportSeries.trim().isEmpty())
                throw new IllegalArgumentException("passportSeries не может отсутствовать");
            if (certificateNumber == null || certificateNumber.trim().isEmpty())
                throw new IllegalArgumentException("certificateNumber не может отсутствовать");
            if (certificateSeries == null || certificateSeries.trim().isEmpty())
                throw new IllegalArgumentException("certificateSeries не может отсутствовать");

            return new TransportImpl(this);
        }
    }
}
