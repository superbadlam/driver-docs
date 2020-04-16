package ru.driverdocs.ui.data;

import javafx.beans.property.*;
import ru.driverdocs.domain.DriverLicense;

import java.time.LocalDate;

public class DriverLicenseImpl implements DriverLicense {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty series = new SimpleStringProperty();
    private final StringProperty number = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startdate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> enddate = new SimpleObjectProperty<>();

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
