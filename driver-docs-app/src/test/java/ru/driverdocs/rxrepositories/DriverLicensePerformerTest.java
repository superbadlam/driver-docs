package ru.driverdocs.rxrepositories;

import org.junit.jupiter.api.Test;
import ru.driverdocs.ui.validator.DriverLicenseValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DriverLicensePerformerTest {

    DriverLicenseValidator validator = new DriverLicenseValidator();

    @Test
    void testIsValidSeries() {
        assertTrue(validator.isValidSeries("74 14"));
    }

    @Test
    void testIsValidSeriesWhenSeriesIsNull() {
        assertThrows(NullPointerException.class, () -> validator.isValidSeries(null));
    }

    @Test
    void testIsValidSeriesWhenWrongFormat() {
        assertFalse(validator.isValidSeries(""));
        assertFalse(validator.isValidSeries("   "));
        assertFalse(validator.isValidSeries("1234"));
        assertFalse(validator.isValidSeries("hdkjhsdkjf"));
        assertFalse(validator.isValidSeries("12j23"));
        assertFalse(validator.isValidSeries("12 23234"));
        assertFalse(validator.isValidSeries("12 34qwe"));
        assertFalse(validator.isValidSeries("12_23"));
    }

    @Test
    void testIsValidNumber() {
        assertTrue(validator.isValidNumber("292010"));
    }

    @Test
    void testIsValidNumberWhenNumberIsNUll() {
        assertThrows(NullPointerException.class, () -> validator.isValidNumber(null));
    }

    @Test
    void testIsValidNumberWhenWrongFormat() {
        assertFalse(validator.isValidNumber(""));
        assertFalse(validator.isValidNumber("   "));
        assertFalse(validator.isValidNumber("12345"));
        assertFalse(validator.isValidNumber(" 123456"));
        assertFalse(validator.isValidNumber("123456 "));
        assertFalse(validator.isValidNumber("1234567"));
        assertFalse(validator.isValidNumber("any_string"));
        assertFalse(validator.isValidNumber("123 45"));
    }

    @Test
    void testIsValidDateRange() {
        assertTrue(validator.isValidDateRange(LocalDate.now(), LocalDate.now().plusYears(10)));
    }

    @Test
    void testIsValidDateRangeWhenEnddateBeforeThenStartDate() {
        assertFalse(validator.isValidDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
    }

    @Test
    void testIsValidDateRangeWhenDiffBetweenIsWrong() {
        assertFalse(validator.isValidDateRange(LocalDate.now(), LocalDate.now().plusYears(9)));
        assertFalse(validator.isValidDateRange(LocalDate.now(), LocalDate.now().plusYears(11)));
    }

    @Test
    void testIsValidDateRangeWhenStartDateIsAfterCurrentDate() {
        assertFalse(validator.isValidDateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1).plusYears(11)));
    }


}