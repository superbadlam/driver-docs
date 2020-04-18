package ru.driverdocs.rxrepositories.impl;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.EmployerLicense;
import ru.driverdocs.rxrepositories.EmployerLicenseRepository;
import ru.driverdocs.rxrepositories.data.EmployerLicenseImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import static ru.driverdocs.helpers.ConvUtils.date2LocalDate;
import static ru.driverdocs.helpers.ConvUtils.toSqlDate;

public final class EmployerLicenseRepositoryImpl implements EmployerLicenseRepository {

    private static final Logger log = LoggerFactory.getLogger(EmployerLicenseRepositoryImpl.class);
    private final Database db;

    public EmployerLicenseRepositoryImpl(Database db) {
        this.db = db;
    }

    @Override
    public Single<Long> create(long employerId, String series, String number, LocalDate startdate) {

        log.trace("выполним создание предпринимательской лицензии: employerId={}, series={}, number={}, startdate={}",
                employerId, series, number, startdate);

        java.sql.Date sdate = toSqlDate(startdate);
        return
                db.update("insert " +
                        "into dd.employer_license(employer_id, license_series,license_number,startdate) " +
                        "values(?,?,?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(employerId, series, number, sdate)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать предпринимательскую лицензию: employerId=%d, series=%s, number=%s, startdate=%s",
                                                employerId, series, number, startdate), e)
                        )
                        .singleOrError();
    }

    @Override
    public Completable delete(long licenseId) {
        log.trace("выполним удаление предпринимательской лицензии: employer-license-id={}", licenseId);
        return
                db.update("delete from dd.employer_license where keyid=?")
                        .parameter(licenseId)
                        .complete()
                        .doOnError(e -> log.warn("не удалось удалить предпринимательскую лицензию: id=" + licenseId, e));
    }

    @Override
    public Single<EmployerLicense> findByEmployerId(long employerId) {

        return
                db.select("select keyid, license_series,license_number,startdate " +
                        "from dd.employer_license " +
                        "where employer_id=?")
                        .parameter(employerId)
                        .getAs(Long.class, String.class, String.class, Date.class)
                        .doOnError(e -> log.warn(String.format("не удалось получитьпредпринимательскую лицензию: employer-id=%d", employerId), e))
                        .map(row -> buildLicense(row.value1(), row.value2(), row.value3(), row.value4()))
                        .singleOrError();
    }

    private EmployerLicense buildLicense(Long id, String series, String number, Date start) {
        return EmployerLicenseImpl.createOf(id, series, number, date2LocalDate(start));
    }

    @Override
    public Completable update(long id, String series, String number, LocalDate startdate) {

        log.trace("выполним обновление предпринимательской лицензии: id={}, series={}, number={}, startdate={}",
                id, series, number, startdate);

        java.sql.Date sdate = toSqlDate(startdate);
        return
                db.update("update dd.employer_license " +
                        "set license_series=?, license_number=?, startdate=? " +
                        "where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(series, number, sdate, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить предпринимательскую лицензию: " +
                                                        "id=%d, new-series=%s, new-number=%s, new-startdate=%s",
                                                id, series, number, startdate), e)
                        );
    }
}
