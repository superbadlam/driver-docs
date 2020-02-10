package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import ru.driverdocs.domain.Route;

public interface RouteRepository {
    Single<Long> create(String name);

    Completable delete(long routeId);

    Flowable<Route> findAll();

    Completable update(long id, String name);
}
