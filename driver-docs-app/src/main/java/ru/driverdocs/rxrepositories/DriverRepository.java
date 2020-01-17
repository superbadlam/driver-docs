package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import ru.driverdocs.domain.Driver;

import java.time.LocalDate;

public interface DriverRepository {
    Single<Long> create(String firstname, String lastname, String secondname, LocalDate birthdate);

    Completable delete(long driverId);

    Flowable<Driver> findAll();

    Completable update(long key, String lastname, String firstname, String secondname, LocalDate birthdate);

    Completable updateBirthdate(long key, LocalDate birthdate);

    Completable updateSecondname(long key, String secondname);

    Completable updateLastname(long key, String lastname);

    Completable updateFirstname(long key, String firstname);
}
