package ru.driverdocs.rxrepositories.impl;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.davidmoten.rx.jdbc.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployerOnRouteRepositoryImplTest extends CommonRepositoryTest {
    @BeforeEach
    public void beforeEach() {
        testWithDatabase(this::clearTables);
    }

    private void clearTables(Database db) {
        db.update("delete from dd.route_employer").counts()
                .concatWith(db.update("delete from dd.route").counts())
                .concatWith(db.update("delete from dd.employer").counts())
                .blockingSubscribe();
    }


    @Test
    public void testCreate() {

        testWithDatabase(db -> {
            EmployerOnRouteRepositoryImpl employerOnRouteRepository = new EmployerOnRouteRepositoryImpl(db);
            EmployerRepositoryImpl employerRepository = new EmployerRepositoryImpl(db);
            RouteRepositoryImpl routeRepository = new RouteRepositoryImpl(db);

            Single<Long> emplOnRouteIdSingle =
                    createEmployerOnRoute(employerOnRouteRepository, employerRepository, routeRepository);

            TestObserver<Long> testObserver = new TestObserver<>();
            emplOnRouteIdSingle.subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertSubscribed()
                    .assertComplete()
                    .assertNoErrors()
                    .assertValueCount(1);

        });
    }

    private Single<Long> createEmployerOnRoute(EmployerOnRouteRepositoryImpl employerOnRouteRepository, EmployerRepositoryImpl employerRepository, RouteRepositoryImpl routeRepository) {
        String emplName = "name of employer";
        String inn = "inn number";
        String ogrn = "ogrn number";
        String address = "employer address";
        String routeName = "route name";

        return employerRepository.create(emplName, inn, ogrn, address)
                .zipWith(routeRepository.create(routeName), this::createMyTuple2Of)
                .flatMap(x -> employerOnRouteRepository.create(x.getValue1(), x.getValue2()));
    }

    @Test
    public void testDelete() {
        testWithDatabase(db -> {
            EmployerOnRouteRepositoryImpl employerOnRouteRepository = new EmployerOnRouteRepositoryImpl(db);
            EmployerRepositoryImpl employerRepository = new EmployerRepositoryImpl(db);
            RouteRepositoryImpl routeRepository = new RouteRepositoryImpl(db);

            TestObserver<Void> testObserver = new TestObserver<>();
            createEmployerOnRoute(employerOnRouteRepository, employerRepository, routeRepository)
                    .flatMapCompletable(employerOnRouteRepository::delete)
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertSubscribed()
                    .assertComplete()
                    .assertNoErrors();

        });
    }

    @Test
    public void testDeleteWhenIdDoesNotExist() {
        testWithDatabase(db -> {
            EmployerOnRouteRepositoryImpl employerOnRouteRepository
                    = new EmployerOnRouteRepositoryImpl(db);

            TestObserver<Void> testObserver = new TestObserver<>();
            employerOnRouteRepository.delete(10)
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertSubscribed()
                    .assertComplete()
                    .assertNoErrors();

        });
    }

}