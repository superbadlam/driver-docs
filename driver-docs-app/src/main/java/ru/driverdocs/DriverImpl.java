package ru.driverdocs;

import ru.driverdocs.domain.Driver;

import java.time.LocalDate;

public class DriverImpl implements Driver {
    private long id;
    private String firstname;
    private String lastname;
    private String secondname;
    private LocalDate birthdate;

    public DriverImpl(long id, String lastname, String firstname, String secondname, LocalDate birthdate) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.secondname = secondname;
        this.birthdate = birthdate;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    @Override
    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
}
