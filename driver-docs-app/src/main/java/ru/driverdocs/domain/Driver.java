package ru.driverdocs.domain;

import java.time.LocalDate;
import java.util.List;

public interface Driver {
    long getId();
    String getLastname();
    String getFirstName();
    String getSecondname();
    LocalDate getBirthdate();
    List<String> getContacts();
    DriverLicense getDriverLicense();
    MedicalReference getMedicalReference();
}
