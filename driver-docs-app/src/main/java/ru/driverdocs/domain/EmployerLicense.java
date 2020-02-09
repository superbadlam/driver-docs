package ru.driverdocs.domain;

import java.time.LocalDate;

public interface EmployerLicense {
    long getId();

    String getSeries();

    String getNumber();

    LocalDate getStartdate();
}
