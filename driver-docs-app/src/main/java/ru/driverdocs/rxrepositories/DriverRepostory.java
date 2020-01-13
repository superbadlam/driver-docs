package ru.driverdocs.rxrepositories;

import io.reactivex.Single;
import ru.driverdocs.domain.Driver;

import java.time.LocalDate;

public interface DriverRepostory {
    Single<Driver> create(String firstname, String lastname, String secondname, LocalDate birthdate);

}