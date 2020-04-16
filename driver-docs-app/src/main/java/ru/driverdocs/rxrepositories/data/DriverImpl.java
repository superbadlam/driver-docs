package ru.driverdocs.rxrepositories.data;

import ru.driverdocs.domain.Driver;

import java.time.LocalDate;

public final class DriverImpl implements Driver {

    final private long id;
    final private String firstname;
    final private String lastname;
    final private String secondname;
    final private LocalDate birthdate;

    private DriverImpl(Builder builder) {
        super();

        birthdate = builder.birthdate;
        firstname = builder.firstname.trim();
        id = builder.id;
        lastname = builder.lastname.trim();
        secondname = (builder.secondname == null) ? "" : builder.secondname.trim();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    @Override
    public String getFirstname() {
        return firstname;
    }

    @Override
    public String getSecondname() {
        return secondname;
    }

    @Override
    public LocalDate getBirthdate() {
        return birthdate;
    }

    public static class Builder {
        private long id;
        private String firstname;
        private String lastname;
        private String secondname;
        private LocalDate birthdate;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder setLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public Builder setSecondname(String secondname) {
            this.secondname = secondname;
            return this;
        }

        public Builder setBirthdate(LocalDate birthdate) {
            this.birthdate = birthdate;
            return this;
        }

        public DriverImpl build() {
            if (id <= 0)
                throw new IllegalArgumentException("id не может принимать значения меньше единицы");
            if (firstname == null || firstname.trim().isEmpty())
                throw new IllegalArgumentException("firstname не может отсутствовать");
            if (lastname == null || lastname.trim().isEmpty())
                throw new IllegalArgumentException("lastname не может отсутствовать");
            if (birthdate == null)
                throw new IllegalArgumentException("birthdate не может отсутствовать");

            return new DriverImpl(this);
        }
    }
}