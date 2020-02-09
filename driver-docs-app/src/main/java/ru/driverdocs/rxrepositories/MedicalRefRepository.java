package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.driverdocs.domain.MedicalReference;

import java.time.LocalDate;

public interface MedicalRefRepository {
    Single<Long> create(long driverId, String series, String number, LocalDate startdate);

    Completable delete(long medicalRefId);

    Single<MedicalReference> findByDriverId(long driverId);

    Completable update(long id, String series, String number, LocalDate startdate);
}
