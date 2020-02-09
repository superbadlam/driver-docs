package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.MedicalReference;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import static ru.driverdocs.helpers.ConvUtils.date2LocalDate;
import static ru.driverdocs.helpers.ConvUtils.toSqlDate;

public final class MedicalRefRepositoryImpl implements MedicalRefRepository {

    private static Logger log = LoggerFactory.getLogger(MedicalRefRepositoryImpl.class);
    private final Database db;

    public MedicalRefRepositoryImpl(Database db) {
        this.db = db;
    }

    @Override
    public Single<Long> create(long driverId, String series, String number, LocalDate startdate) {

        log.trace("выполним создание мед. правки: driverId={}, series={}, number={}, startdate={}",
                driverId, series, number, startdate);

        java.sql.Date sdate = toSqlDate(startdate);
        return
                db.update("insert into dd.medical_reference(driver_id, ref_series, ref_number, startdate) " +
                        "values(?,?,?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(driverId, series, number, sdate)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать мед. правку: driverId=%d, series=%s, number=%s, startdate=%s",
                                                driverId, series, number, startdate), e)
                        )
                        .singleOrError();
    }

    @Override
    public Completable delete(long medicalRefId) {
        log.trace("выполним удаление мед. справки: driver-license-id={}", medicalRefId);
        return
                db.update("delete from dd.medical_reference where keyid=?")
                        .parameter(medicalRefId)
                        .complete()
                        .doOnError(e -> log.warn("не удалось удалить мед. справку: id=" + medicalRefId, e));
    }

    @Override
    public Single<MedicalReference> findByDriverId(long driverId) {

        return
                db.select("select keyid, ref_series, ref_number, startdate " +
                        "from dd.medical_reference " +
                        "where driver_id=?")
                        .parameter(driverId)
                        .getAs(Long.class, String.class, String.class, Date.class)
                        .doOnError(e -> log.warn(String.format("не удалось найти мед. справку: driver-id=%d", driverId), e))
                        .map(row -> buildDriverLicense(row.value1(), row.value2(), row.value3(), row.value4()))
                        .singleOrError();
    }

    private MedicalReference buildDriverLicense(Long id, String series, String number, Date start) {
        return MedicalReferenceImpl.createOf(id, series, number, date2LocalDate(start));
    }

    @Override
    public Completable update(long id, String series, String number, LocalDate startdate) {

        log.trace("выполним обновление мед. справки: id={}, series={}, number={}, startdate={}",
                id, series, number, startdate);

        java.sql.Date sdate = toSqlDate(startdate);
        return
                db.update("update dd.medical_reference set ref_series=?, ref_number=?, startdate=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(series, number, sdate, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить мед. справку: " +
                                                        "id=%d, new-series=%s, new-number=%s, new-startdate=%s",
                                                id, series, number, startdate), e)
                        );
    }
}
