package ru.driverdocs.rxrepositories;

import org.junit.jupiter.api.Test;
import ru.driverdocs.ui.validator.DriverLicenseValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DriverLicenseValidatePerformerTest {

    @Test
    void testIsValidSeries() {
        assertTrue(DriverLicenseValidator.isValidSeries("74 14"));
    }

    @Test
    void testIsValidSeriesWhenSeriesIsNull() {
        assertThrows(NullPointerException.class, () -> DriverLicenseValidator.isValidSeries(null));
    }

    @Test
    void testIsValidSeriesWhenWrongFormat() {
        assertFalse(DriverLicenseValidator.isValidSeries(""));
        assertFalse(DriverLicenseValidator.isValidSeries("   "));
        assertFalse(DriverLicenseValidator.isValidSeries("1234"));
        assertFalse(DriverLicenseValidator.isValidSeries("hdkjhsdkjf"));
        assertFalse(DriverLicenseValidator.isValidSeries("12j23"));
        assertFalse(DriverLicenseValidator.isValidSeries("12 23234"));
        assertFalse(DriverLicenseValidator.isValidSeries("12 34qwe"));
        assertFalse(DriverLicenseValidator.isValidSeries("12_23"));
    }

    @Test
    void testIsValidNumber() {
        assertTrue(DriverLicenseValidator.isValidNumber("292010"));
    }

    @Test
    void testIsValidNumberWhenNumberIsNUll() {
        assertThrows(NullPointerException.class, () -> DriverLicenseValidator.isValidNumber(null));
    }

    @Test
    void testIsValidNumberWhenWrongFormat() {
        assertFalse(DriverLicenseValidator.isValidNumber(""));
        assertFalse(DriverLicenseValidator.isValidNumber("   "));
        assertFalse(DriverLicenseValidator.isValidNumber("12345"));
        assertFalse(DriverLicenseValidator.isValidNumber(" 123456"));
        assertFalse(DriverLicenseValidator.isValidNumber("123456 "));
        assertFalse(DriverLicenseValidator.isValidNumber("1234567"));
        assertFalse(DriverLicenseValidator.isValidNumber("any_string"));
        assertFalse(DriverLicenseValidator.isValidNumber("123 45"));
    }

    @Test
    void testIsValidDateRange() {
        assertTrue(DriverLicenseValidator.isValidDateRange(LocalDate.now(), LocalDate.now().plusYears(10)));
    }

    @Test
    void testIsValidDateRangeWhenEnddateBeforeThenStartDate() {
        assertFalse(DriverLicenseValidator.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
    }

    @Test
    void testIsValidDateRangeWhenDiffBetweenIsWrong() {
        assertFalse(DriverLicenseValidator.isValidDateRange(LocalDate.now(), LocalDate.now().plusYears(9)));
        assertFalse(DriverLicenseValidator.isValidDateRange(LocalDate.now(), LocalDate.now().plusYears(11)));
    }

    @Test
    void testIsValidDateRangeWhenStartDateIsAfterCurrentDate() {
        assertFalse(DriverLicenseValidator.isValidDateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1).plusYears(11)));
    }


}