package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.DriverLicense;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import static ru.driverdocs.helpers.ConvUtils.date2LocalDate;
import static ru.driverdocs.helpers.ConvUtils.toSqlDate;

public final class DriverLicenseRepositoryImpl implements DriverLicenseRepository {

    private static Logger log = LoggerFactory.getLogger(DriverLicenseRepositoryImpl.class);
    private final Database db;

    public DriverLicenseRepositoryImpl(Database db) {
        this.db = db;
    }

    @Override
    public Single<Long> create(long driverId, String series, String number, LocalDate startdate, LocalDate enddate) {

        log.trace("выполним создание водительского удостоверения: driverId={}, series={}, number={}, startdate={}, enddate={}",
                driverId, series, number, startdate, enddate);

        java.sql.Date sdate = toSqlDate(startdate);
        java.sql.Date edate = toSqlDate(enddate);
        return
                db.update("insert into dd.driver_license(driver_id, license_series,license_number,startdate,enddate) " +
                        "values(?,?,?,?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(driverId, series, number, sdate, edate)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать водительское удостоверение: driverId=%d, series=%s, number=%s, startdate=%s, enddate=%s",
                                                driverId, series, number, startdate, enddate), e)
                        )
                        .singleOrError();
    }

    @Override
    public Completable delete(long licenseId) {
        log.trace("выполним удаление вод. удостоверения: driver-license-id={}", licenseId);
        return
                db.update("delete from dd.driver_license where keyid=?")
                        .parameter(licenseId)
                        .complete()
                        .doOnError(e -> log.warn("не удалось удалить вод. удостоверение: id=" + licenseId, e));
    }

    @Override
    public Single<DriverLicense> findByDriverId(long driverId) {

        return
                db.select("select keyid, license_series,license_number,startdate,enddate from dd.driver_license where driver_id=?")
                        .parameter(driverId)
                        .getAs(Long.class, String.class, String.class, Date.class, Date.class)
                        .doOnError(e -> log.warn(String.format("не удалось получить вод. удостоверение для водителя: driver-id=%d", driverId), e))
                        .map(row -> buildDriverLicense(row.value1(), row.value2(), row.value3(), row.value4(), row.value5()))
                        .singleOrError();
    }

    private DriverLicense buildDriverLicense(Long id, String series, String number, Date start, Date end) {
        return new DriverLicenseImpl.Builder()
                .setId(id)
                .setSeries(series)
                .setNumber(number)
                .setStartdate(date2LocalDate(start))
                .setEnddate(date2LocalDate(end))
                .build();
    }

    @Override
    public Completable updateEnddate(long key, LocalDate enddate) {

        log.info("выполним обновление даты окончания действия вод. удостоверения: license-id={}: new-enddate={}",
                key, enddate);

        java.sql.Date edate = toSqlDate(enddate);
        return
                db.update("update dd.driver_license set enddate=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(edate, key)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить дату окончания действия вод. удостоверения: license-id=%d: new-enddate=%s",
                                                key, enddate), e)
                        );
    }

    @Override
    public Completable updateStartdate(long key, LocalDate startdate) {

        log.info("выполним обновление даты начала действия вод. удостоверения: license-id={}: new-startdate={}",
                key, startdate);

        java.sql.Date sdate = toSqlDate(startdate);
        return
                db.update("update dd.driver_license set startdate=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(sdate, key)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить дату начала действия вод. удостоверения: license-id=%d: new-startdate=%s",
                                                key, startdate), e)
                        );
    }

    @Override
    public Completable updateSeries(long key, String series) {

        log.info("выполним обновление серии водительского удостоверения: license-id={}: new-series={}", key, series);

        return
                db.update("update dd.driver_license set license_series=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(series, key)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить серию водительского удостоверения: license-id=%d: new-series=%s"
                                                , key, series), e)
                        );
    }

    @Override
    public Completable updateNumber(long key, String number) {

        log.info("выполним обновление номера водительского удостоверения: license-id={}: new-number={}", key, number);

        return
                db.update("update dd.driver_license set license_number=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(number, key)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить номер водительского удостоверения: license-id=%d: new-number=%s"
                                                , key, number), e)
                        );
    }

    @Override
    public Completable update(long id, String series, String number, LocalDate startdate, LocalDate enddate) {

        log.trace("выполним обновление водительского удостоверения: id={}, series={}, number={}, startdate={}, enddate={}",
                id, series, number, startdate, enddate);

        java.sql.Date sdate = toSqlDate(startdate);
        java.sql.Date edate = toSqlDate(enddate);
        return
                db.update("update dd.driver_license set license_series=?, license_number=?, startdate=?, enddate=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(series, number, sdate, edate, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить водительское удостоверение: " +
                                                        "id=%d, new-series=%s, new-number=%s, new-startdate=%s, new-enddate=%s",
                                                id, series, number, startdate, enddate), e)
                        );
    }
}
