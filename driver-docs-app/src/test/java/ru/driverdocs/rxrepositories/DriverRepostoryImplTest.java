package ru.driverdocs.rxrepositories;

import io.reactivex.Flowable;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import ru.driverdocs.domain.Driver;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DriverRepostoryImplTest {

    private final int maxPoolSize = 1;
    private final String url = "jdbc:h2:./db-test/dd";
    private final String user = "sa";
    private final String password = "driver-docs";


    @Test
    void findAll() {
        final String prefixLastname = "фамилия-";
        final String prefixFirstname = "имя-";
        final String prefixSecondname = "отчество-";
        final LocalDate birthdate = LocalDate.now();
        final int suffix11 = 11;
        final int suffix12 = 12;
        final int suffix13 = 13;

        Flowable<List<?>> rows = Flowable.just(
                Arrays.asList(prefixLastname + suffix11, prefixFirstname + suffix11, prefixSecondname + suffix11, birthdate),
                Arrays.asList(prefixLastname + suffix12, prefixFirstname + suffix12, prefixSecondname + suffix12, birthdate),
                Arrays.asList(prefixLastname + suffix13, prefixFirstname + suffix13, prefixSecondname + suffix13, birthdate)
        );
        Flowable<Integer> suffixes = Flowable.just(suffix11, suffix12, suffix13);

        try (Database db = Database
                .nonBlocking()
                .user(user)
                .password(password)
                .url(url)
                .maxPoolSize(maxPoolSize)
                .build()) {

            final long l = db.update("delete from dd.driver")
                    .counts().blockingSingle().longValue();
            //-------------------------------------------------------
            Map<Long, Tuple2<Long, Integer>> keys = db.update("insert into dd.driver(lastname,firstname,secondname,birthdate) values(?,?,?,?)")
                    .parameterListStream(rows)
                    .returnGeneratedKeys()
                    .getAs(Long.class)
                    .zipWith(suffixes, Tuple2::create)
                    .toMap(Tuple2::value1)
                    .blockingGet();

            DriverRepostoryImpl repository = new DriverRepostoryImpl(db);
            Map<Long, Driver> data = repository.findAll()
                    .toMap(Driver::getId)
                    .blockingGet();

            keys.keySet().forEach(keyid->{
                assertTrue(data.containsKey(keyid));
                assertEquals(prefixLastname + keys.get(keyid).value2(), data.get(keyid).getLastname());
                assertEquals(prefixFirstname + keys.get(keyid).value2(), data.get(keyid).getFirstname());
                assertEquals(prefixSecondname + keys.get(keyid).value2(), data.get(keyid).getSecondname());
                assertEquals( birthdate,  data.get(keyid).getBirthdate());
            });

            assertEquals(3,
                db.update("delete from dd.driver where keyid in (?)")
                  .parameters(keys.keySet())
                  .counts().blockingSingle().intValue()
            );
            //--------------------------------------------------------

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void create() {
        final String lastname = "фамилия-02";
        final String firstname = "имя-02";
        final String secondname = "отчество-02";
        final LocalDate birthdate = LocalDate.now();

        try (Database db = Database
                .nonBlocking()
                .user(user)
                .password(password)
                .url(url)
                .maxPoolSize(maxPoolSize)
                .build()) {

            DriverRepostoryImpl repostory = new DriverRepostoryImpl(db);
            Driver driver = repostory.create(lastname, firstname, secondname, birthdate).blockingGet();
            assertTrue(driver.getId() > 0);

            db.select("select d.lastname, d.firstname, d.secondname from dd.driver d where d.keyid=?")
                    .parameters(driver.getId())
                    .getAs(String.class, String.class, String.class)
                    .blockingForEach(t -> {
                        assertEquals(lastname, t._1());
                        assertEquals(firstname, t._2());
                        assertEquals(secondname, t._3());
                    });

            Integer integer = db
                    .update("delete from dd.driver where keyid=?")
                    .parameter(driver.getId())
                    .counts()
                    .blockingSingle();
            assertEquals(1, (int) integer);

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void delete() {
        final String lastname = "фамилия-02";
        final String firstname = "имя-02";
        final String secondname = "отчество-02";
        final LocalDate birthdate = LocalDate.now();


        try (Database db = Database
                .nonBlocking()
                .user(user)
                .password(password)
                .url(url)
                .maxPoolSize(maxPoolSize)
                .build()) {


            long keyid =
                    db.update("insert into dd.driver(lastname,firstname,secondname,birthdate) values(?,?,?,?)")
                            .parameterListStream(Flowable.just(Arrays.asList(lastname, firstname, secondname, birthdate)))
                            .returnGeneratedKeys()
                            .getAs(Long.class)
                            .singleOrError()
                            .map(key -> key)
                            .blockingGet();
            assertTrue(keyid > 0);

            DriverRepostoryImpl repostory = new DriverRepostoryImpl(db);
            Boolean isDeleted = repostory.delete(keyid).blockingGet();
            assertTrue(isDeleted);

        } catch (Exception e) {
            fail(e);
        }
    }
}