package ru.driverdocs.rxrepositories.impl;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.davidmoten.rx.jdbc.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.driverdocs.domain.Employer;
import ru.driverdocs.domain.EmployerOnRoute;
import ru.driverdocs.rxrepositories.EmployerRepository;
import ru.driverdocs.rxrepositories.RouteRepository;
import ru.driverdocs.rxrepositories.data.EmployerImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private List<Long> createEmployers(EmployerRepository employerRepository, List<Employer> employers) {
        List<Long> employerIds = new ArrayList<>();
        Flowable.fromIterable(employers)
                .flatMapSingle(empl -> employerRepository.create(empl.getName(), empl.getInn(), empl.getOgrn(), empl.getOgrn()))
                .blockingIterable().forEach(employerIds::add);
        return employerIds;
    }

    private List<Long> createRoutes(RouteRepository routeRepository, List<String> routeNames) {
        List<Long> routeIds = new ArrayList<>();
        Flowable.fromIterable(routeNames)
                .flatMapSingle(routeRepository::create)
                .blockingIterable().forEach(routeIds::add);
        return routeIds;
    }

//    private List<Long> createEmployerRouteBindings(EmployerOnRouteRepositoryImpl employerOnRouteRepository,
//                                                           List<Long> employerIds,
//                                                           List<Long> routeIds) {
//        List<Long> employerRouteBindings=new ArrayList<>();
//         employerOnRouteRepository.create(employerIds.get(0), routeIds.get(0))
//                .concatWith(employerOnRouteRepository.create(employerIds.get(0), routeIds.get(1)))
//                .concatWith(employerOnRouteRepository.create(employerIds.get(0), routeIds.get(2)))
//                .concatWith(employerOnRouteRepository.create(employerIds.get(1), routeIds.get(0)))
//                .concatWith(employerOnRouteRepository.create(employerIds.get(1), routeIds.get(1)))
//                .blockingIterable().forEach(employerRouteBindings::add);
//         return employerRouteBindings;
//    }

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

    private void assertFindAllByXXX(Flowable<Long> employerOnRouteIdsFlowable, Long... results) {
        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
        employerOnRouteIdsFlowable.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertSubscribed()
                .assertComplete()
                .assertNoErrors()
                .assertResult(results);
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

    @Test
    public void testFindAllByEmployer() {
        final List<Employer> employers = Arrays.asList(
                new EmployerImpl.Builder()
                        .setOgrn("1177746415857")
                        .setName("Иванов Иван Иванович")
                        .setInn("7704407589")
                        .setAddress("Город улица дом квартира")
                        .setId(0)
                        .build(),
                new EmployerImpl.Builder()
                        .setOgrn("1177746415858")
                        .setName("Петров Петр Петрович")
                        .setInn("7704407580")
                        .setAddress("Город улица дом квартира")
                        .setId(0)
                        .build());
        final List<String> routeNames = Arrays.asList("route#1", "route#2", "route#3");

        testWithDatabase(db -> {
            EmployerOnRouteRepositoryImpl employerOnRouteRepository = new EmployerOnRouteRepositoryImpl(db);
            List<Long> employerIds = createEmployers(new EmployerRepositoryImpl(db), employers);
            List<Long> routeIds = createRoutes(new RouteRepositoryImpl(db), routeNames);

            employerOnRouteRepository.create(employerIds.get(0), routeIds.get(0))
                    .concatWith(employerOnRouteRepository.create(employerIds.get(0), routeIds.get(1)))
                    .concatWith(employerOnRouteRepository.create(employerIds.get(0), routeIds.get(2)))
                    .concatWith(employerOnRouteRepository.create(employerIds.get(1), routeIds.get(0)))
                    .concatWith(employerOnRouteRepository.create(employerIds.get(1), routeIds.get(1)))
                    .blockingSubscribe();

            Flowable<Long> routes4FirstEmployer = employerOnRouteRepository
                    .findAllByEmployerId(employerIds.get(0))
                    .map(EmployerOnRoute::getRouteId);
            Flowable<Long> routes4SecondEmployer = employerOnRouteRepository
                    .findAllByEmployerId(employerIds.get(1))
                    .map(EmployerOnRoute::getRouteId);
            assertFindAllByXXX(routes4FirstEmployer, routeIds.toArray(new Long[0]));
            assertFindAllByXXX(routes4SecondEmployer, routeIds.get(0), routeIds.get(1));
        });
    }

    @Test
    public void testFindAllByRoute() {
        final List<Employer> employers = Arrays.asList(
                new EmployerImpl.Builder()
                        .setOgrn("1177746415857")
                        .setName("Иванов Иван Иванович")
                        .setInn("7704407589")
                        .setAddress("Город улица дом квартира")
                        .setId(0)
                        .build(),
                new EmployerImpl.Builder()
                        .setOgrn("1177746415858")
                        .setName("Петров Петр Петрович")
                        .setInn("7704407580")
                        .setAddress("Город улица дом квартира")
                        .setId(0)
                        .build());
        final List<String> routeNames = Arrays.asList("route#1", "route#2", "route#3");

        testWithDatabase(db -> {
            EmployerOnRouteRepositoryImpl employerOnRouteRepository = new EmployerOnRouteRepositoryImpl(db);
            List<Long> employerIds = createEmployers(new EmployerRepositoryImpl(db), employers);
            List<Long> routeIds = createRoutes(new RouteRepositoryImpl(db), routeNames);

            employerOnRouteRepository.create(employerIds.get(0), routeIds.get(0))
                    .concatWith(employerOnRouteRepository.create(employerIds.get(0), routeIds.get(1)))
                    .concatWith(employerOnRouteRepository.create(employerIds.get(1), routeIds.get(0)))
                    .blockingSubscribe();

            Flowable<Long> employers4FirstRoute = employerOnRouteRepository
                    .findAllByRouteId(routeIds.get(0))
                    .map(EmployerOnRoute::getEmployerId);
            Flowable<Long> employers4SecondRoute = employerOnRouteRepository
                    .findAllByRouteId(routeIds.get(1))
                    .map(EmployerOnRoute::getEmployerId);
            Flowable<Long> employers4ThirdRoute = employerOnRouteRepository
                    .findAllByRouteId(routeIds.get(2))
                    .map(EmployerOnRoute::getEmployerId);
            assertFindAllByXXX(employers4FirstRoute, employerIds.toArray(new Long[0]));
            assertFindAllByXXX(employers4SecondRoute, employerIds.get(0));
            assertFindAllByXXX(employers4ThirdRoute);
        });
    }

    @Test
    public void testFindAllByEmployerWhenEmployerIdNotFound() {

        testWithDatabase(db -> {
            EmployerOnRouteRepositoryImpl employerOnRouteRepository = new EmployerOnRouteRepositoryImpl(db);

            TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
            employerOnRouteRepository.findAllByEmployerId(0)
                    .map(EmployerOnRoute::getRouteId)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertSubscribed()
                    .assertComplete()
                    .assertNoErrors()
                    .assertNoValues();

        });
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