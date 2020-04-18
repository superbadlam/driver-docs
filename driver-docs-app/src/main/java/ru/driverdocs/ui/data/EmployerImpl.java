package ru.driverdocs.ui.data;

import javafx.beans.property.*;
import ru.driverdocs.domain.Employer;
import ru.driverdocs.ui.validator.EmployerValidator;

public final class EmployerImpl implements Employer {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty inn = new SimpleStringProperty();
    private final StringProperty ogrn = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final BooleanProperty invalid = new SimpleBooleanProperty();
    private final EmployerValidator validator = new EmployerValidator();

    public EmployerImpl() {
        resetState();
        setInvalid(true);
        name.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(newValue, address.get(), inn.get(), ogrn.get())));
        address.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(name.get(), newValue, inn.get(), ogrn.get())));
        inn.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(name.get(), address.get(), newValue, ogrn.get())));
        ogrn.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(name.get(), address.get(), inn.get(), newValue)));
    }

    public static EmployerImpl createOf(Employer employer) {
        EmployerImpl newEmployer = new EmployerImpl();
        newEmployer.setAddress(employer.getAddress());
        newEmployer.setName(employer.getName());
        newEmployer.setOgrn(employer.getOgrn());
        newEmployer.setInn(employer.getInn());
        newEmployer.setId(employer.getId());
        return newEmployer;
    }

    private boolean isInvalid(String name, String address, String inn, String ogrn) {
        return !validator.isValid(name, address, inn, ogrn);
    }

    public void resetState() {
        setId(0);
        setInn("");
        setOgrn("");
        setName("");
        setAddress("");
    }

    public void copyState(Employer employer) {
        setAddress(employer.getAddress());
        setName(employer.getName());
        setOgrn(employer.getOgrn());
        setInn(employer.getInn());
        setId(employer.getId());
    }

    public BooleanProperty invalidProperty() {
        return invalid;
    }

    private void setInvalid(boolean valid) {
        this.invalid.set(valid);
    }

    @Override
    public long getId() {
        return id.get();
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public LongProperty idProperty() {
        return id;
    }

    @Override
    public String getInn() {
        return inn.get();
    }

    public void setInn(String inn) {
        this.inn.set(inn);
    }

    public StringProperty innProperty() {
        return inn;
    }

    @Override
    public String getOgrn() {
        return ogrn.get();
    }

    public void setOgrn(String ogrn) {
        this.ogrn.set(ogrn);
    }

    public StringProperty ogrnProperty() {
        return ogrn;
    }

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }
}
