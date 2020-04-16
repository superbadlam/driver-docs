package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import ru.driverdocs.domain.EmployerOnRoute;

public interface EmployerOnRouteRepository {
    Single<Long> create(long employerId, long routeId);

    Completable delete(long id);

    Flowable<EmployerOnRoute> findAllByEmployerId(long employerId);

    Flowable<EmployerOnRoute> findAllByRouteId(long routeId);
}
