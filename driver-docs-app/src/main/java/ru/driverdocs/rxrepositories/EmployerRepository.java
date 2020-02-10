package ru.driverdocs.rxrepositories;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import ru.driverdocs.domain.Employer;

public interface EmployerRepository {
    Single<Long> create(String name, String inn, String ogrn, String address);

    Completable delete(long employerId);

    Flowable<Employer> findAll();

    Completable updateName(long id, String name);

    Completable updateInn(long id, String inn);

    Completable updateOgrn(long id, String ogrn);

    Completable updateAddress(long key, String address);
}
