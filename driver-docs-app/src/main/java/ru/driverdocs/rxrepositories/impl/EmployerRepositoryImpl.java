package ru.driverdocs.rxrepositories.impl;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.Employer;
import ru.driverdocs.rxrepositories.EmployerRepository;
import ru.driverdocs.rxrepositories.data.EmployerImpl;

import java.util.Arrays;

public class EmployerRepositoryImpl implements EmployerRepository {
    private static final Logger log = LoggerFactory.getLogger(DriverRepositoryImpl.class);
    private final Database db;

    public EmployerRepositoryImpl(Database db) {
        if (db == null)
            throw new NullPointerException("объект базы данных является обязательным");
        this.db = db;
    }

    @Override
    public Single<Long> create(String name, String inn, String ogrn, String address) {

        log.trace("выполним создание предпринимателя: name={}, inn={}, ogrn={}, address={}", name, inn, ogrn, address);

        return
                db.update("insert into dd.employer(name,inn,ogrn,address) values(?,?,?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(name, inn, ogrn, address)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать водителя: name=%s, inn=%s, ogrn=%s, address=%s",
                                                name, inn, ogrn, address), e)
                        )
                        .singleOrError();
    }


    @Override
    public Completable delete(long employerId) {
        log.trace("выполним удаление предпринимателя с id={}", employerId);
        return
                db.update("delete from dd.employer where keyid=?")
                        .parameter(employerId)
                        .complete()
                        .doOnError(key -> log.warn("не удалось удалить предпринимателя: id=" + employerId, key));
    }

    @Override
    public Flowable<Employer> findAll() {
        return
                db.select("select e.keyid, e.name, e.inn, e.ogrn, e.address from dd.employer e")
                        .getAs(Long.class, String.class, String.class, String.class, String.class)
                        .doOnError(e -> log.warn("не удалось получить полный список предпринимателей", e))
                        .map(row -> new EmployerImpl.Builder()
                                .setId(row.value1())
                                .setName(row.value2())
                                .setInn(row.value3())
                                .setOgrn(row.value4())
                                .setAddress(row.value5())
                                .build());
    }


    @Override
    public Completable updateName(long id, String name) {

        log.info("выполним обновление имени предпринимателя: id={}, new-name={}",
                id, name);
        return
                db.update("update dd.employer set name=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(name, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить имени предпринимателя: id=%d: new-name=%s",
                                                id, name), e)
                        );
    }

    @Override
    public Completable updateInn(long id, String inn) {

        log.info("выполним обновление ИНН предпринимателя: id={}, new-inn={}", id, inn);

        return
                db.update("update dd.employer set inn=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(inn, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить обновление ИНН предпринимателя: id=%d: new-inn=%s", id, inn), e)
                        );
    }

    @Override
    public Completable updateOgrn(long key, String ogrn) {

        log.info("выполним обновление ОГРН предпринимателя: id={}, new-ogrn={}", key, ogrn);

        return
                db.update("update dd.employer set ogrn=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(ogrn, key)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить ОГРН предпринимателя: id=%d: new-ogrn=%s", key, ogrn), e)
                        );
    }

    @Override
    public Completable updateAddress(long key, String address) {

        log.info("выполним обновление адреса предпринимателя: id={}, new-address={}", key, address);

        return
                db.update("update dd.employer set address=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(address, key)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить адрес предпринимателя: id=%d, new-address=%s", key, address), e)
                        );
    }
}
