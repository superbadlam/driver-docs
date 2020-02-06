package ru.driverdocs.domain;

import java.time.LocalDate;

public interface MedicalReference {
    long getId();
    String getSeries();
    String getNumber();
    LocalDate getStartdate();
}
