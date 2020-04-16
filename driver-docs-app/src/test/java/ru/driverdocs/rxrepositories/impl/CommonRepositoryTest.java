package ru.driverdocs.rxrepositories.impl;

import org.davidmoten.rx.jdbc.Database;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

public class CommonRepositoryTest {
    protected final int maxPoolSize = 1;
    protected final String url = "jdbc:h2:./db-test/dd";
    protected final String user = "sa";
    protected final String password = "driver-docs";

    protected <T1, T2> MyTuple2<T1, T2> createMyTuple2Of(T1 o1, T2 o2) {
        return new MyTuple2<>(o1, o2);
    }

    protected void testWithDatabase(Consumer<Database> consumer) {
        try (Database db = Database
                .nonBlocking()
                .user(user)
                .password(password)
                .url(url)
                .maxPoolSize(maxPoolSize)
                .build()) {

            consumer.accept(db);

        } catch (Exception e) {
            fail(e);
        }
    }

    public static final class MyTuple2<T1, T2> {
        private T1 o1;
        private T2 o2;

        private MyTuple2(T1 o1, T2 o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        public T2 getValue2() {
            return o2;
        }

        public T1 getValue1() {
            return o1;
        }
    }

}
