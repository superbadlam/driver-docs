package ru.driverdocs.ui.data;

import javafx.beans.property.*;
import ru.driverdocs.domain.MedicalReference;
import ru.driverdocs.ui.validator.MedicalRefValidator;

import java.time.LocalDate;

public class MedicalReferenceImpl implements MedicalReference {
    private final ObjectProperty<LocalDate> startdate = new SimpleObjectProperty<>();
    private final StringProperty number = new SimpleStringProperty();
    private final StringProperty series = new SimpleStringProperty();
    private final LongProperty id = new SimpleLongProperty();
    private final BooleanProperty invalid = new SimpleBooleanProperty();
    private final MedicalRefValidator validator = new MedicalRefValidator();


    public MedicalReferenceImpl() {
        setInvalid(true);
        number.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(newValue, series.get(), startdate.get())));
        series.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(number.get(), newValue, startdate.get())));
        startdate.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(number.get(), series.get(), newValue)));

    }

    private boolean isInvalid(String number, String series, LocalDate startdate) {
        return !validator.isValid(number, number, startdate);
    }

    public BooleanProperty invalidProperty() {
        return invalid;
    }

    private void setInvalid(boolean valid) {
        this.invalid.set(valid);
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
    public String toString() {
        return "MedicalReferenceImpl{" +
                "startdate=" + startdate.get() +
                ", number=" + number.get() +
                ", series=" + series.get() +
                ", id=" + id.get() +
                '}';
    }
}
