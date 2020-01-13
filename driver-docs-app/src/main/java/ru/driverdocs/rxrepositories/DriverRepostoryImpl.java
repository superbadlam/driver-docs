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

public class DriverRepostoryImpl implements DriverRepostory {
    private static Logger log = LoggerFactory.getLogger(DriverRepostoryImpl.class);
    private Database db;

    public DriverRepostoryImpl(Database db) {
        if (db == null)
            throw new NullPointerException("db can't be a null");
        this.db = db;
    }

    private LocalDate date2LocalDate(Date date) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        return instant.atZone(defaultZoneId).toLocalDate();
    }

    @Override
    public Single<Driver> create(String lastname, String firstname, String secondname, LocalDate birthdate) {
        return
                db.update("insert into dd.driver(lastname,firstname,secondname,birthdate) values(?,?,?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(lastname, firstname, secondname, java.sql.Date.valueOf(birthdate))))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e -> log.warn("не удалось создать водителя", e))
                        .singleOrError()
                        .doOnSuccess(key -> log.info("создали нового водителя: id={}", key))
                        .map(key -> new DriverImpl.Builder()
                                .setId(key)
                                .setLastname(lastname)
                                .setFirstname(firstname)
                                .setSecondname(secondname)
                                .setBirthdate(birthdate)
                                .build());
    }

    @Override
    public Single<Boolean> delete(long driverId) {
        return
                db.update("delete from dd.driver where keyid=?")
                        .parameter(driverId)
                        .counts()
                        .singleOrError()
                        .doOnSuccess(key -> log.info("удалили водителя: id={}", key))
                        .map(key -> key > 0);
    }

    @Override
    public Flowable<Driver> findAll() {
        return
            db.select("select d.keyid, d.lastname, d.firstname, d.secondname, d.birthdate from dd.driver d")
                    .getAs(Long.class, String.class, String.class, String.class, Date.class)
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
        return
                db.update("update dd.driver set lastname=?, firstname=?, secondname=?, birthdate=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(lastname, firstname, secondname,java.sql.Date.valueOf(birthdate),key)))
                        .complete();
    }
}
