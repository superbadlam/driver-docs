package ru.driverdocs.ui;

import javafx.beans.property.*;
import ru.driverdocs.domain.MedicalReference;

import java.time.LocalDate;

class MedicalReferenceImpl implements MedicalReference {
    private ObjectProperty<LocalDate> startdate = new SimpleObjectProperty<>();
    private StringProperty number = new SimpleStringProperty();
    private StringProperty series = new SimpleStringProperty();
    private LongProperty id = new SimpleLongProperty();

    @Override
    public LocalDate getStartdate() {
        return startdate.get();
    }

    void setStartdate(LocalDate startdate) {
        this.startdate.set(startdate);
    }

    ObjectProperty<LocalDate> startdateProperty() {
        return startdate;
    }

    @Override
    public String getNumber() {
        return number.get();
    }

    void setNumber(String number) {
        this.number.set(number);
    }

    StringProperty numberProperty() {
        return number;
    }

    @Override
    public String getSeries() {
        return series.get();
    }

    void setSeries(String series) {
        this.series.set(series);
    }

    StringProperty seriesProperty() {
        return series;
    }

    @Override
    public long getId() {
        return id.get();
    }

    public void setId(long id) {
        this.id.set(id);
    }

    LongProperty idProperty() {
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
