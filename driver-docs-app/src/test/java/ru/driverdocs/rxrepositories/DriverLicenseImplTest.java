package ru.driverdocs.rxrepositories;

import org.junit.jupiter.api.Test;
import ru.driverdocs.rxrepositories.data.DriverLicenseImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DriverLicenseImplTest {
    static final LocalDate startdate = LocalDate.now().minusYears(1);
    static final LocalDate enddate = LocalDate.now().minusDays(1);
    static final String series = "серия";
    static final String number = "номер";
    static final long id = 1000;

    @Test
    private void testBuild() {
        DriverLicenseImpl d = new DriverLicenseImpl.Builder()
                .setId(id)
                .setNumber(number)
                .setSeries(series)
                .setStartdate(startdate)
                .setEnddate(enddate)
                .build();

        assertEquals(id, d.getId());
        assertEquals(number, d.getNumber());
        assertEquals(series, d.getSeries());
        assertEquals(startdate, d.getStartdate());
        assertEquals(enddate, d.getEnddate());
    }


}