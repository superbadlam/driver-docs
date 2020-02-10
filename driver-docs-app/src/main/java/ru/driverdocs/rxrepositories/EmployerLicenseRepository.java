package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.driverdocs.domain.EmployerLicense;

import java.time.LocalDate;

public interface EmployerLicenseRepository {
    Single<Long> create(long employerId, String series, String number, LocalDate startdate);

    Completable delete(long licenseId);

    Single<EmployerLicense> findByEmployerId(long employerId);

    Completable update(long id, String series, String number, LocalDate startdate);
}
