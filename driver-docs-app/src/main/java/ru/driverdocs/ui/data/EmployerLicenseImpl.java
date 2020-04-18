package ru.driverdocs.ui.data;

import javafx.beans.property.*;
import ru.driverdocs.domain.EmployerLicense;
import ru.driverdocs.ui.validator.EmployerLicenseValidator;

import java.time.LocalDate;

public class EmployerLicenseImpl implements EmployerLicense {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty series = new SimpleStringProperty();
    private final StringProperty number = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startdate = new SimpleObjectProperty<>();
    private final BooleanProperty invalid = new SimpleBooleanProperty();
    private final EmployerLicenseValidator validator = new EmployerLicenseValidator();

    public EmployerLicenseImpl() {
        setInvalid(true);
        number.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(newValue, series.get(), startdate.get())));
        series.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(number.get(), newValue, startdate.get())));
        startdate.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(number.get(), series.get(), newValue)));
    }

    private boolean isInvalid(String number, String series, LocalDate startdate) {
        return !validator.isValid(number, series, startdate);
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
    public String getSeries() {
        return series.get();
    }

    public void setSeries(String series) {
        this.series.set(series);
    }

    public StringProperty seriesProperty() {
        return series;
    }

    @Override
    public String getNumber() {
        return number.get();
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public StringProperty numberProperty() {
        return number;
    }

    @Override
    public LocalDate getStartdate() {
        return startdate.get();
    }

    public void setStartdate(LocalDate startdate) {
        this.startdate.set(startdate);
    }

    public ObjectProperty<LocalDate> startdateProperty() {
        return startdate;
    }

    @Override
    public String toString() {
        return "EmployerLicenseImpl{" +
                "id=" + id.get() +
                ", series=" + series.get() +
                ", number=" + number.get() +
                ", startdate=" + startdate.get() +
                '}';
    }
}
