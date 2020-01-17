package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.Driver;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public final class DriverRepositoryImpl implements DriverRepository {

    private static Logger log = LoggerFactory.getLogger(DriverRepositoryImpl.class);
    private final Database db;

    public DriverRepositoryImpl(Database db) {
        if (db == null)
            throw new NullPointerException("объект базы данных является обязательным");
        this.db = db;
    }

    private LocalDate date2LocalDate(Date date) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        return instant.atZone(defaultZoneId).toLocalDate();
    }

    @Override
    public Single<Long> create(String lastname, String firstname, String secondname, LocalDate birthdate) {

        log.trace("выполним создание водителя: lastname={}, firstname={}, secondname={}, birthdate={}", lastname, firstname, secondname, birthdate);

        java.sql.Date bd = birthdate == null ? null : java.sql.Date.valueOf(birthdate);
        return
                db.update("insert into dd.driver(lastname,firstname,secondname,birthdate) values(?,?,?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(lastname, firstname, secondname, bd)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать водителя: lastname=%s, firstname=%s, secondname=%s, birthdate=%s",
                                                lastname, firstname, secondname, birthdate), e)
                        )
                        .singleOrError();
    }

    @Override
    public Single<Boolean> delete(long driverId) {
        log.trace("выполним удаление водителя с id={}", driverId);
        return
                db.update("delete from dd.driver where keyid=?")
                        .parameter(driverId)
                        .counts()
                        .singleOrError()
                        .doOnError(key -> log.warn("не удалось удалить водителя: id=" + driverId, key))
                        .map(key -> key > 0);
    }

    @Override
    public Flowable<Driver> findAll() {
        return
                db.select("select d.keyid, d.lastname, d.firstname, d.secondname, d.birthdate from dd.driver d")
                        .getAs(Long.class, String.class, String.class, String.class, Date.class)
                        .doOnError(e -> log.warn("не удалось получить полный список водителей", e))
                        .map(row -> new DriverImpl.Builder()
                                .setId(row.value1())
                                .setLastname(row.value2())
                                .setFirstname(row.value3())
                                .setSecondname(row.value4())
                                .setBirthdate(date2LocalDate(row.value5()))
                                .build());
    }

    @Override
    public Completable update(long key, String lastname, String firstname, String secondname, LocalDate birthdate) {

        log.info("выполним обновление водителя с id={}: new-lastname={}, new-firstname={}, new-secondname={}, new-birthdate={}",
                key, lastname, firstname, secondname, birthdate);

        java.sql.Date bd = birthdate == null ? null : java.sql.Date.valueOf(birthdate);
        return
                db.update("update dd.driver set lastname=?, firstname=?, secondname=?, birthdate=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(lastname, firstname, secondname, bd, key)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить водителя с id=%d: new-lastname=%s, new-firstname=%s, new-secondname=%s, new-birthdate=%s",
                                                key, lastname, firstname, secondname, birthdate), e)
                        );
    }
}