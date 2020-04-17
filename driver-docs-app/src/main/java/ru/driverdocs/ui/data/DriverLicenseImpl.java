package ru.driverdocs.ui.data;

import javafx.beans.property.*;
import ru.driverdocs.domain.DriverLicense;
import ru.driverdocs.ui.validator.DriverLicenseValidator;

import java.time.LocalDate;

public class DriverLicenseImpl implements DriverLicense {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty series = new SimpleStringProperty();
    private final StringProperty number = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startdate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> enddate = new SimpleObjectProperty<>();
    private final BooleanProperty invalid = new SimpleBooleanProperty();

    public DriverLicenseImpl() {
        setInvalid(true);
        number.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(newValue, series.get(), startdate.get(), enddate.get())));
        series.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(number.get(), newValue, startdate.get(), enddate.get())));
        startdate.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(number.get(), series.get(), newValue, enddate.get())));
        enddate.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(number.get(), series.get(), startdate.get(), newValue)));
    }

    private boolean isValid(String number, String series, LocalDate startdate, LocalDate enddate) {
        return DriverLicenseValidator.isValidNumber(number)
                && DriverLicenseValidator.isValidSeries(series)
                && DriverLicenseValidator.isValidDateRange(startdate, enddate);
    }

    private boolean isInvalid(String number, String series, LocalDate startdate, LocalDate enddate) {
        return !isValid(number, series, startdate, enddate);
    }

//    public boolean isValid() {
//        return valid.get();
//    }

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
    public LocalDate getEnddate() {
        return enddate.get();
    }

    public void setEnddate(LocalDate enddate) {
        this.enddate.set(enddate);
    }

    public ObjectProperty<LocalDate> enddateProperty() {
        return enddate;
    }

    @Override
    public String toString() {
        return "DriverLicenseImpl{" +
                "id=" + id.get() +
                ", series=" + series.get() +
                ", number=" + number.get() +
                ", startdate=" + startdate.get() +
                ", enddate=" + enddate.get() +
                '}';
    }
}
