package ru.driverdocs.domain;

import java.time.LocalDate;

public interface DriverLicense {
    long getId();
    String getSeries();
    String getNumber();
    LocalDate getStartdate();
    LocalDate getEnddate();
    Driver getDriver();
}
