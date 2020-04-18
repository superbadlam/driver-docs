package ru.driverdocs.ui.validator;

import java.util.regex.Pattern;

public class EmployerValidator {
    private static final Pattern INN_PATTERN = Pattern.compile("([0-9]{12})");
    private static final Pattern OGRN_PATTERN = Pattern.compile("([0-9]{13})");

    public boolean isValidInn(String inn) {
        return inn != null
                && INN_PATTERN.matcher(inn).matches();
    }

    public boolean isValidOgrn(String ogrn) {
        return ogrn != null
                && OGRN_PATTERN.matcher(ogrn).matches();
    }

    public boolean isValidName(String name) {
        return name != null
                && !name.isEmpty();
    }

    public boolean isValidAddress(String address) {
        return address != null
                && !address.isEmpty();
    }

    public boolean isValid(String name, String address, String inn, String ogrn) {
        return isValidInn(inn)
                && isValidOgrn(ogrn)
                && isValidAddress(address)
                && isValidName(name);
    }
}
