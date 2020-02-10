package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import ru.driverdocs.domain.Transport;

public interface TransportRepository {
    Single<Long> create(
            long employerId,
            String plateNo,
            String marka,
            String model,
            int seats,
            String passportSeries,
            String passportNumber,
            String certificateSeries,
            String certificateNumber);

    Completable delete(long transportId);

    Flowable<Transport> findByEmployerId(long employerId);

    Completable updatePlateNo(long id, String plateNo);

    Completable updateMarka(long id, String marka);

    Completable updateModel(long id, String model);

    Completable updateSeats(long id, int seats);

    Completable updatePassportSeries(long id, String passportSeries);

    Completable updatePassportNumber(long id, String passportNumber);

    Completable updateCertificateSeries(long id, String certificateSeries);

    Completable updateCertificateNumber(long id, String certificateNumber);
}
