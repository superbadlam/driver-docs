package ru.driverdocs.domain;

import java.time.LocalDate;

public interface Driver {
    long getId();
    String getLastname();
    String getFirstname();
    String getSecondname();
    LocalDate getBirthdate();
}
