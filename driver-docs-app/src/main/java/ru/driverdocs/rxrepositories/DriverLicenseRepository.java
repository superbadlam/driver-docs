package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.driverdocs.domain.DriverLicense;

import java.time.LocalDate;

public interface DriverLicenseRepository {
    Single<Long> create(String series, String number, LocalDate startdate);

    Completable delete(long licenseId);

    Single<DriverLicense> findByDriverId(long driverId);

    Completable updateEnddate(long key, LocalDate enddate);

    Completable updateStartdate(long key, LocalDate startdate);

    Completable updateSeries(long key, String series);

    Completable updateNumber(long key, String number);
}
