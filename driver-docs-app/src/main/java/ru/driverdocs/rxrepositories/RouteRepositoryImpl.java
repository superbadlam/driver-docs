package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.Route;

import java.util.Arrays;

public final class RouteRepositoryImpl implements RouteRepository {

    private static Logger log = LoggerFactory.getLogger(RouteRepositoryImpl.class);
    private final Database db;

    public RouteRepositoryImpl(Database db) {
        if (db == null)
            throw new NullPointerException("объект базы данных является обязательным");
        this.db = db;
    }


    @Override
    public Single<Long> create(String name) {

        log.trace("выполним создание маршрута: name={}", name);
        return
                db.update("insert into dd.route(name) values(?)")
                        .parameter(name)
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать маршрут: name=%s", name), e)
                        )
                        .singleOrError();
    }


    @Override
    public Completable delete(long routeId) {
        log.trace("выполним удаление маршрута: id={}", routeId);
        return
                db.update("delete from dd.route where keyid=?")
                        .parameter(routeId)
                        .complete()
                        .doOnError(key -> log.warn("не удалось удалить маршрут: id=" + routeId, key));
    }

    @Override
    public Flowable<Route> findAll() {
        return
                db.select("select d.keyid, d.name from dd.route d")
                        .getAs(Long.class, String.class)
                        .doOnError(e -> log.warn("не удалось получить полный список маршрутов", e))
                        .map(row -> buildRoute(row.value1(), row.value2()));
    }

    private Route buildRoute(Long id, String name) {
        return RouteImpl.createOf(id, name);
    }

    @Override
    public Completable update(long id, String name) {

        log.info("выполним обновление маршрута:  id={}, new-name={}", id, name);
        return
                db.update("update dd.route set name=? where keyid=?")
                        .parameterListStream(Flowable.just(Arrays.asList(name, id)))
                        .complete()
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось обновить маршрут: id=%d: new-name=%s", id, name), e)
                        );
    }

}
