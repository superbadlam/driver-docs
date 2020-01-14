package ru.driverdocs.rxrepositories;

import io.reactivex.Flowable;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.tuple.Tuple2;
import org.h2.jdbc.JdbcBatchUpdateException;
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
    void updateBithdateWithNull() {
        updateAnyFieldWithNull("фамилия-101", "имя-101", "отчество-101", null);
    }

    @Test
    void updateSecondNameWithNull() {
        updateAnyFieldWithNull("фамилия-101", "имя-101", null, LocalDate.now());
    }

    @Test
    void updateFirstNameWithNull() {
        updateAnyFieldWithNull("фамилия-101", null, "отчество-101", LocalDate.now());
    }

    @Test
    void updateLastNameWithNull() {
        updateAnyFieldWithNull(null, "имя-101", "отчество-101", LocalDate.now());
    }

    private void updateAnyFieldWithNull(String lastname2, String firstname2, String secondname2, LocalDate birthdate2) {
        final String lastname1 = "фамилия-100";
        final String firstname1 = "имя-100";
        final String secondname1 = "отчество-100";
        final LocalDate birthdate1 = LocalDate.now().minusDays(1);

        try (Database db = Database
                .nonBlocking()
                .user(user).password(password)
                .url(url).maxPoolSize(maxPoolSize)
                .build()) {

            long key = insertIntoDriver(lastname1, firstname1, secondname1, birthdate1, db);

            DriverRepostoryImpl repository = new DriverRepostoryImpl(db);
            assertThrows(JdbcBatchUpdateException.class, () -> {
                throw repository.update(key, lastname2, firstname2, secondname2, birthdate2).blockingGet();
            });

            Integer integer = deleteFromDriver(db, key);
            assertEquals(1, (int) integer);

            //--------------------------------------------------------

        } catch (Exception e) {
            fail(e);
        }
    }

    private Integer deleteFromDriver(Database db, long key) {
        return db
                .update("delete from dd.driver where keyid=?")
                .parameter(key)
                .counts()
                .blockingSingle();
    }

    private long insertIntoDriver(String lastname1, String firstname1, String secondname1, LocalDate birthdate1, Database db) {
        return db.update("insert into dd.driver(lastname,firstname,secondname,birthdate) values(?,?,?,?)")
                .parameterListStream(Flowable.just(Arrays.asList(lastname1, firstname1, secondname1, birthdate1)))
                .returnGeneratedKeys()
                .getAs(Long.class)
                .blockingSingle();
    }

    @Test
    void update() {
        final String lastname1 = "фамилия-100";
        final String firstname1 = "имя-100";
        final String secondname1 = "отчество-100";
        final LocalDate birthdate1 = LocalDate.now().minusDays(1);

        final String lastname2 = "фамилия-101";
        final String firstname2 = "имя-101";
        final String secondname2 = "отчество-101";
        final LocalDate birthdate2 = LocalDate.now();


        try (Database db = Database
                .nonBlocking()
                .user(user).password(password)
                .url(url).maxPoolSize(maxPoolSize)
                .build()) {

            long key = insertIntoDriver(lastname1, firstname1, secondname1, birthdate1, db);

            DriverRepostoryImpl repository = new DriverRepostoryImpl(db);
            repository.update(key, lastname2, firstname2, secondname2, birthdate2).blockingAwait();


            db.select("select d.lastname, d.firstname, d.secondname from dd.driver d where d.keyid=?")
                    .parameters(key)
                    .getAs(String.class, String.class, String.class)
                    .blockingForEach(t -> {
                        assertEquals(lastname2, t._1());
                        assertEquals(firstname2, t._2());
                        assertEquals(secondname2, t._3());
                    });

            Integer integer = deleteFromDriver(db, key);
            assertEquals(1, (int) integer);

            //--------------------------------------------------------

        } catch (Exception e) {
            fail(e);
        }
    }


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

            clearTableDriver(db);
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

            keys.keySet().forEach(keyid -> {
                assertTrue(data.containsKey(keyid));
                assertEquals(prefixLastname + keys.get(keyid).value2(), data.get(keyid).getLastname());
                assertEquals(prefixFirstname + keys.get(keyid).value2(), data.get(keyid).getFirstname());
                assertEquals(prefixSecondname + keys.get(keyid).value2(), data.get(keyid).getSecondname());
                assertEquals(birthdate, data.get(keyid).getBirthdate());
            });

            assertEquals(keys.size(),
                    db.update("delete from dd.driver where keyid in (?)")
                            .parameters(keys.keySet())
                            .counts().blockingSingle().intValue()
            );
            //--------------------------------------------------------

        } catch (Exception e) {
            fail(e);
        }
    }

    private void clearTableDriver(Database db) {
        db.update("delete from dd.driver")
                .counts().blockingSingle().longValue();
    }


    @Test
    void createWhenLastnameIsNull() {
        createWhenAnyFieldIsNull(null, "имя-02", "отчество-02", LocalDate.now());
    }

    @Test
    void createWhenFirstnameIsNull() {
        createWhenAnyFieldIsNull("фамилия-02", null, "отчество-02", LocalDate.now());
    }

    @Test
    void createWhenSecondnameIsNull() {
        createWhenAnyFieldIsNull("фамилия-02", "имя-02", null, LocalDate.now());
    }

    @Test
    void createWhenBirthdateIsNull() {
        createWhenAnyFieldIsNull("фамилия-02", "имя-02", "отчество-02", null);
    }


    private void createWhenAnyFieldIsNull(String lastname, String firstname, String secondname, LocalDate birthdate) {
        try (Database db = Database
                .nonBlocking()
                .user(user)
                .password(password)
                .url(url)
                .maxPoolSize(maxPoolSize)
                .build()) {

            DriverRepostoryImpl repository = new DriverRepostoryImpl(db);
            assertThrows(RuntimeException.class, () -> repository.create(lastname, firstname, secondname, birthdate).blockingGet());

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
            long driverId = repostory.create(lastname, firstname, secondname, birthdate).blockingGet();
            assertTrue(driverId > 0);

            db.select("select d.lastname, d.firstname, d.secondname from dd.driver d where d.keyid=?")
                    .parameters(driverId)
                    .getAs(String.class, String.class, String.class)
                    .blockingForEach(t -> {
                        assertEquals(lastname, t._1());
                        assertEquals(firstname, t._2());
                        assertEquals(secondname, t._3());
                    });

            Integer integer = deleteFromDriver(db, driverId);
            assertEquals(1, (int) integer);

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void deleteWhenIdDoesNotExists() {
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

            clearTableDriver(db);

            long keyid = insertIntoDriver(lastname, firstname, secondname, birthdate, db);
            assertTrue(keyid > 0);

            DriverRepostoryImpl repostory = new DriverRepostoryImpl(db);
            Boolean isDeleted = repostory.delete(keyid - 1).blockingGet();
            assertFalse(isDeleted);

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

            long keyid = insertIntoDriver(lastname, firstname, secondname, birthdate, db);
            assertTrue(keyid > 0);

            DriverRepostoryImpl repostory = new DriverRepostoryImpl(db);
            Boolean isDeleted = repostory.delete(keyid).blockingGet();
            assertTrue(isDeleted);

        } catch (Exception e) {
            fail(e);
        }
    }
}