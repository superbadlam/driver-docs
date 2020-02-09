package ru.driverdocs.domain;

public interface Transport {
    long getId();

    String getPlateNo();

    String getMarka();

    String getModel();

    int getSeats();

    String getPassportSeries();

    String getPassportNumber();

    String getCertificateSeries();

    String getCertificateNumber();
}
