package ru.driverdocs.rxrepositories.impl;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.domain.EmployerOnRoute;
import ru.driverdocs.rxrepositories.EmployerOnRouteRepository;
import ru.driverdocs.rxrepositories.data.EmployerOnRouteImpl;

import java.util.Arrays;

public class EmployerOnRouteRepositoryImpl implements EmployerOnRouteRepository {
    private static final Logger log = LoggerFactory.getLogger(EmployerLicenseRepositoryImpl.class);
    private final Database db;

    public EmployerOnRouteRepositoryImpl(Database db) {
        this.db = db;
    }

    @Override
    public Single<Long> create(long employerId, long routeId) {

        log.trace("выполним создание связки предпринимателя и маршрута:" +
                " employerId={}, routeId={}", employerId, routeId);

        return
                db.update("insert into dd.route_employer(employer_id, route_id) " +
                        "values(?,?)")
                        .parameterListStream(Flowable.just(Arrays.asList(employerId, routeId)))
                        .returnGeneratedKeys()
                        .getAs(Long.class)
                        .doOnError(e ->
                                log.warn(
                                        String.format(
                                                "не удалось создать связку предпринимателя и маршрута: " +
                                                        "employerId=%d, routeId=%d",
                                                employerId, routeId), e)
                        )
                        .singleOrError();
    }

    @Override
    public Completable delete(long id) {
        log.trace("выполним удаление связки предпринимателя и маршрута: " +
                "employer-on-route-id={}", id);
        return
                db.update("delete from dd.route_employer where keyid=?")
                        .parameter(id)
                        .complete()
                        .doOnError(e -> log.warn("не удалось удалить связку предпринимателя и маршрута: " +
                                "id=" + id, e));
    }

    @Override
    public Flowable<EmployerOnRoute> findAllByEmployerId(long employerId) {
        log.trace("выполним поиск маршрутов предпринимателя: " +
                "employer-id={}", employerId);
        return
                db.select("select d.keyid, d.employer_id, d.route_id " +
                        "from dd.route_employer d where d.employer_id=?")
                        .parameter(employerId)
                        .getAs(Long.class, Long.class, Long.class)
                        .doOnError(e -> log.warn("не удалось найти маршруты по предпринимателю:" +
                                " employer-id=" + employerId, e))
                        .map(row -> EmployerOnRouteImpl.createOf(
                                row.value1(), row.value2(), row.value3()
                        ));
    }

    @Override
    public Flowable<EmployerOnRoute> findAllByRouteId(long routeId) {
        log.trace("выполним поиск предпринимателей ассоциированных с маршрутом: " +
                "route-id={}", routeId);
        return
                db.select("select d.keyid, d.employer_id, d.route_id " +
                        "from dd.route_employer d where d.route_id=?")
                        .parameter(routeId)
                        .getAs(Long.class, Long.class, Long.class)
                        .doOnError(e -> log.warn("не удалось найти предпринимателей по маршруту", e))
                        .map(row -> EmployerOnRouteImpl.createOf(
                                row.value1(), row.value2(), row.value3()
                        ));
    }
}
