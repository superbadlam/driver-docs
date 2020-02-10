package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.Transport;

import java.util.Arrays;

public final class TransportRepositoryImpl implements TransportRepository {

    private static Logger log = LoggerFactory.getLogger(TransportRepositoryImpl.class);
    private final Database db;

    public TransportRepositoryImpl(Database db) {
        if (db == null)
            throw new NullPointerException("объект базы данных является обязательным");
        this.db = db;
    }


    @Override
    public Single<Long> create(long employerId,
                               String plateNo,
                               String marka,
                               String model,
                               int seats,
                               String passportSeries,
                               String passportNumber,
                               String certificateSeries,
                               String certificateNumber) {

        log.trace("выполним создание транспорта: employerId={}," +
                        "plateNo={}," +
                        "marka={}," +
                        "model={}," +
                        "seats={}," +
                        "passportSeries={}," +
                        "passportNumber={}," +
                        "certificateSeries={}," +
                        "certificateNumber={}",
                employerId,
                plateNo,
                marka,
                model,
                seats,
                passportSeries,
                passportNumber,
                certificateSeries,
                certificateNumber);
        return
                db.update("insert into dd.car(" +
                        "plate_no," +
                        "marka," +
                        "model," +
                        "seats," +
                        "passport_series," +
                        "passport_number," +
                        "certificate_series," +
                        "certificate_number," +
                        "employer_id) " +
                        "values(?,?,?,?,?,?,?,?,?)")
                        .parameterListStream(
                                Flowable.just(Arrays.asList(
                                        plateNo, marka, model, seats,
                                        passportSeries, passportNumber,
                                        certificateSeries, certificateNumber, employerId)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать транспорт: employerId=%d," +
                                                        "plateNo=%s," +
                                                        "marka=%s," +
                                                        "model=%s," +
                                                        "seats=%d," +
                                                        "passportSeries=%s," +
                                                        "passportNumber=%s," +
                                                        "certificateSeries=%s," +
                                                        "certificateNumber=%s",
                                                employerId,
                                                plateNo,
                                                marka,
                                                model,
                                                seats,
                                                passportSeries,
                                                passportNumber,
                                                certificateSeries,
                                                certificateNumber), e)
                        )
                        .singleOrError();
    }


    @Override
    public Completable delete(long transportId) {
        log.trace("выполним удаление транспорта: id={}", transportId);
        return
                db.update("delete from dd.car where keyid=?")
                        .parameter(transportId)
                        .complete()
                        .doOnError(key -> log.warn("не удалось удалить транспорт: id=" + transportId, key));
    }

    @Override
    public Flowable<Transport> findByEmployerId(long employerId) {
        return
                db.select("select " +
                        "plate_no," +
                        "marka," +
                        "model," +
                        "seats," +
                        "passport_series," +
                        "passport_number," +
                        "certificate_series," +
                        "certificate_number" +
                        " from dd.car" +
                        " where employer_id=?")
                        .get(rs -> (Transport) new TransportImpl.Builder()
                                .setPlateNo(rs.getString(1))
                                .setMarka(rs.getString(2))
                                .setModel(rs.getString(3))
                                .setSeats(rs.getInt(4))
                                .setPassportSeries(rs.getString(5))
                                .setPassportNumber(rs.getString(6))
                                .setCertificateSeries(rs.getString(7))
                                .setCertificateNumber(rs.getString(8))
                                .build())
                        .doOnError(ex -> log.warn(
                                String.format("не удалось найти транспорт по employerId: employerId=%s", employerId)
                                , ex));
    }

    @Override
    public Completable updateCertificateNumber(long id, String certificateNumber) {

        log.info("выполним обновление номера свид. о рег-и автомобиля:  transport-id={}, new-passportNumber={}", id, certificateNumber);
        return
                db.update("update dd.car set certificate_number=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(certificateNumber, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить номер свид. о рег-и автомобиля: transport-id=%d: new-passportNumber=%s", id, certificateNumber), e)
                        );
    }

    @Override
    public Completable updateCertificateSeries(long id, String certificateSeries) {

        log.info("выполним обновление серии свид. о рег-и автомобиля:  transport-id={}, new-certificateSeries={}", id, certificateSeries);
        return
                db.update("update dd.car set certificate_series=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(certificateSeries, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить серию свид. о рег-и автомобиля: transport-id=%d: new-certificateSeries=%s", id, certificateSeries), e)
                        );
    }

    @Override
    public Completable updatePassportNumber(long id, String passportNumber) {

        log.info("выполним обновление номера ПТС автомобиля:  transport-id={}, new-passportNumber={}", id, passportNumber);
        return
                db.update("update dd.car set passport_number=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(passportNumber, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить номер ПТС автомобиля: transport-id=%d: new-passportNumber=%s", id, passportNumber), e)
                        );
    }

    @Override
    public Completable updatePassportSeries(long id, String passportSeries) {

        log.info("выполним обновление серии ПТС автомобиля:  transport-id={}, new-passportSeries={}", id, passportSeries);
        return
                db.update("update dd.car set passport_series=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(passportSeries, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить серию ПТС автомобиля: transport-id=%d: new-passportSeries=%s", id, passportSeries), e)
                        );
    }

    @Override
    public Completable updateSeats(long id, int seats) {

        log.info("выполним обновление кол-ва мест автомобиля:  transport-id={}, new-seats={}", id, seats);
        return
                db.update("update dd.car set seats=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(seats, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить кол-во мест автомобиля: transport-id=%d: new-seats=%s", id, seats), e)
                        );
    }

    @Override
    public Completable updateModel(long id, String model) {

        log.info("выполним обновление модели автомобиля:  transport-id={}, new-model={}", id, model);
        return
                db.update("update dd.car set model=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(model, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить модели автомобиля: transport-id=%d: new-model=%s", id, model), e)
                        );
    }

    @Override
    public Completable updatePlateNo(long id, String plateNo) {

        log.info("выполним обновление номерного знака:  transport-id={}, new-plateNo={}", id, plateNo);
        return
                db.update("update dd.car set plate_no=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(plateNo, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить номерной знак: transport-id=%d: new-plateNo=%s", id, plateNo), e)
                        );
    }

    @Override
    public Completable updateMarka(long id, String marka) {

        log.info("выполним обновление марки автомобиля:  transport-id={}, new-marka={}", id, marka);
        return
                db.update("update dd.car set marka=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(marka, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить марку автомобиля: transport-id=%d: new-marka=%s", id, marka), e)
                        );
    }
}
