package ru.driverdocs.rxrepositories;

import org.davidmoten.rx.jdbc.Database;
import org.junit.jupiter.api.Test;
import ru.driverdocs.domain.Driver;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DriverRepostoryImplTest {

    @Test
    void create() {
        final String lastname = "фамилия-02";
        final String firstname = "имя-02";
        final String secondname = "отчество-02";
        final LocalDate birthdate = LocalDate.now();

        final int maxPoolSize = 1;
        final String url = "jdbc:h2:./db-test/dd";


        try (Database db = Database
                .nonBlocking()
                .user("sa")
                .password("driver-docs")
                .url(url)
                .maxPoolSize(maxPoolSize)
                .build()) {

//            Integer integer = db.update("delete from dd.driver").counts().blockingSingle();
//            System.out.println("удалили " + integer + " записей");


            DriverRepostoryImpl repostory = new DriverRepostoryImpl(db);
            Driver driver = repostory.create(lastname, firstname, secondname, birthdate).blockingGet();
            assertTrue(driver.getId() > 0);

            db.select("select d.lastname, d.firstname, d.secondname from dd.driver d where d.keyid=?")
                    .parameters(driver.getId())
                    .getAs(String.class, String.class, String.class)
                    .blockingForEach(t -> {
                        System.out.println("===========================================================================");
                        System.out.println(String.format("%s, %s, %s",lastname,firstname,secondname));
                        System.out.println(t);
                        System.out.println("===========================================================================");
                        assertEquals(lastname, t._1());
                        assertEquals(firstname, t._2());
                        assertEquals(secondname, t._3());
                    });
        } catch (Exception e) {
            fail(e);
        }
    }
}