package ru.driverdocs.rxrepositories;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DriverImplTest {
    static final LocalDate birthdate=LocalDate.of(1981,7,4);
    static final String secondname="отчество";
    static final String lastname="имя";
    static final String firstname="фамилия";
    static final long id=1000;

    @Test
    void  testCreateWhenNormalWay(){

        DriverImpl d=new DriverImpl.Builder()
                .setSecondname(secondname)
                .setLastname(lastname)
                .setId(id)
                .setFirstname(firstname)
                .setBirthdate(birthdate)
                .build();

        assertEquals(birthdate,d.getBirthdate());
        assertEquals(secondname,d.getSecondname());
        assertEquals(lastname,d.getLastname());
        assertEquals(firstname,d.getFirstname());
        assertEquals(id,d.getId());
    }
    @Test
    void testCreateWhenIdIsZero(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(0)
                        .setFirstname(firstname)
                        .setBirthdate(birthdate)
                        .build());
    }
    @Test
    void testCreateWhenIdIsNegative(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(-1)
                        .setFirstname(firstname)
                        .setBirthdate(birthdate)
                        .build());
    }

    @Test
    void testCreateWhenIdIsMissed(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setFirstname(firstname)
                        .setBirthdate(birthdate)
                        .build());
    }


    @Test
    void testCreateWhenBirthdateIsNull(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(id)
                        .setFirstname(firstname)
                        .setBirthdate(null)
                        .build());
    }

    @Test
    void testCreateWhenBirthdateIsMissed(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(id)
                        .setFirstname(firstname)
                        .build());
    }

    @Test
    void testCreateWhenFirstnameIsNull(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(id)
                        .setFirstname(null)
                        .setBirthdate(birthdate)
                        .build());
    }

    @Test
    void testCreateWhenFirstnameIsEmpty(){

        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(id)
                        .setFirstname("")
                        .setBirthdate(birthdate)
                        .build());

    }

    @Test
    void testCreateWhenFirstnameIsBlank(){

        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(id)
                        .setFirstname("        ")
                        .setBirthdate(birthdate)
                        .build());
    }

    @Test
    void testCreateWhenFirstnameIsMissed(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(lastname)
                        .setId(id)
                        .setBirthdate(birthdate)
                        .build());
    }


    @Test
    void testCreateWhenLastnameIsNull(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname(null)
                        .setId(id)
                        .setFirstname(firstname)
                        .setBirthdate(birthdate)
                        .build());
    }

    @Test
    void testCreateWhenLastnameIsEmpty(){

        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname("")
                        .setId(id)
                        .setFirstname(firstname)
                        .setBirthdate(birthdate)
                        .build());

    }

    @Test
    void testCreateWhenLastnameIsBlank(){

        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setLastname("        ")
                        .setId(id)
                        .setFirstname(firstname)
                        .setBirthdate(birthdate)
                        .build());
    }

    @Test
    void testCreateWhenLastnameIsMissed(){
        assertThrows(IllegalArgumentException.class,
                ()->new DriverImpl.Builder()
                        .setSecondname(secondname)
                        .setFirstname(firstname)
                        .setId(id)
                        .setBirthdate(birthdate)
                        .build());
    }

    @Test
    void testCreateWhenSecondnameIsNull(){
        DriverImpl d = (new DriverImpl.Builder()
                .setSecondname(null)
                .setLastname(lastname)
                .setId(id)
                .setFirstname(firstname)
                .setBirthdate(birthdate)
                .build());
        assertTrue(d.getSecondname().isEmpty());
    }

    @Test
    void testCreateWhenSecondnameIsEmpty(){
        DriverImpl d = (new DriverImpl.Builder()
                .setSecondname("")
                .setLastname(lastname)
                .setId(id)
                .setFirstname(firstname)
                .setBirthdate(birthdate)
                .build());
        assertTrue(d.getSecondname().isEmpty());
    }

    @Test
    void testCreateWhenSecondnameIsBlank(){

        DriverImpl d = (new DriverImpl.Builder()
                .setSecondname("            ")
                .setLastname(lastname)
                .setId(id)
                .setFirstname(firstname)
                .setBirthdate(birthdate)
                .build());
        assertTrue(d.getSecondname().isEmpty());
    }

    @Test
    void testCreateWhenSecondnameIsMissed(){
        DriverImpl d = (new DriverImpl.Builder()
                .setLastname(lastname)
                .setId(id)
                .setFirstname(firstname)
                .setBirthdate(birthdate)
                .build());
        assertTrue(d.getSecondname().isEmpty());
    }
}