package ru.driverdocs.rxrepositories;

import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.Driver;

import java.time.LocalDate;
import java.util.Arrays;

public class DriverRepostoryImpl implements DriverRepostory{
    private static Logger log = LoggerFactory.getLogger(DriverRepostoryImpl.class);
    private Database db;

    public DriverRepostoryImpl(Database db) {
        if(db==null)
            throw new NullPointerException("db can't be a null");
        this.db = db;
    }

    private Driver buildDriver(long id, String lastname, String firstname, String secondname, LocalDate birthdate){
        return  new DriverImpl.Builder()
                .setSecondname(secondname)
                .setLastname(lastname)
                .setId(id)
                .setFirstname(firstname)
                .setBirthdate(birthdate)
                .build();
    }

    @Override
    public Single<Driver> create(String lastname, String firstname, String secondname, LocalDate birthdate) {
        return
                db.update("insert into dd.driver(lastname,firstname,secondname,birthdate) values(?,?,?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(lastname, firstname, secondname, birthdate)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e->log.warn("не удалось создать водителя",e))
                        .singleOrError()
                        .doOnSuccess(key->log.trace("создали нового водителя с id={}",key))
                        .map(key->buildDriver(key, lastname, firstname, secondname, birthdate));
    }
}
