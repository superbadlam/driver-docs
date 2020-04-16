package ru.driverdocs.rxrepositories.data;

import ru.driverdocs.domain.Employer;

public final class EmployerImpl implements Employer {
    private long id;
    private String inn;
    private String ogrn;
    private String name;
    private String address;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getInn() {
        return inn;
    }

    @Override
    public String getOgrn() {
        return ogrn;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    private EmployerImpl(Builder builder) {
        this.id = builder.id;
        this.inn = builder.inn;
        this.ogrn = builder.ogrn;
        this.name = builder.name;
        this.address = builder.address;
    }

    public static class Builder {
        private long id;
        private String inn;
        private String ogrn;
        private String name;
        private String address;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setInn(String inn) {
            this.inn = inn;
            return this;
        }

        public Builder setOgrn(String ogrn) {
            this.ogrn = ogrn;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public EmployerImpl build() {
            if (id <= 0)
                throw new IllegalArgumentException("id не может принимать значения меньше единицы");
            if (inn == null || inn.trim().isEmpty())
                throw new IllegalArgumentException("инн не может отсутствовать");
            if (ogrn == null || ogrn.trim().isEmpty())
                throw new IllegalArgumentException("огрн не может отсутствовать");
            if (name == null || name.trim().isEmpty())
                throw new IllegalArgumentException("ФИО не может отсутствовать");
            if (address == null || address.trim().isEmpty())
                throw new IllegalArgumentException("адрес не может отсутствовать");
            return new EmployerImpl(this);
        }
    }
}
