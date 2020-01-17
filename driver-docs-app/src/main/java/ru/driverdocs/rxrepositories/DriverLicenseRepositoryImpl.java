package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.DriverLicense;

import java.time.LocalDate;

public final class DriverLicenseRepositoryImpl implements DriverLicenseRepository {

    private static Logger log = LoggerFactory.getLogger(DriverLicenseRepositoryImpl.class);
    private final Database db;

    public DriverLicenseRepositoryImpl(Database db) {
        this.db = db;
    }

    @Override
    public Single<Long> create(String series, String number, LocalDate startdate) {
        return null;
    }

    @Override
    public Completable delete(long licenseId) {
        return null;
    }

    @Override
    public Single<DriverLicense> findByDriverId(long driverId) {
        return null;
    }

    @Override
    public Completable updateEnddate(long key, LocalDate enddate) {
        return null;
    }

    @Override
    public Completable updateStartdate(long key, LocalDate startdate) {
        return null;
    }

    @Override
    public Completable updateSeries(long key, String series) {
        return null;
    }

    @Override
    public Completable updateNumber(long key, String number) {
        return null;
    }
}
