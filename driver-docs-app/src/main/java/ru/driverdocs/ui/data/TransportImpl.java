package ru.driverdocs.ui.data;

import javafx.beans.property.*;
import ru.driverdocs.domain.Transport;
import ru.driverdocs.ui.validator.TransportValidator;

public final class TransportImpl implements Transport {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty plateNo = new SimpleStringProperty();
    private final StringProperty marka = new SimpleStringProperty();
    private final StringProperty model = new SimpleStringProperty();
    private final ObjectProperty<Integer> seats = new SimpleObjectProperty<>();
    private final StringProperty passportSeries = new SimpleStringProperty();
    private final StringProperty passportNumber = new SimpleStringProperty();
    private final StringProperty certificateSeries = new SimpleStringProperty();
    private final StringProperty certificateNumber = new SimpleStringProperty();

    private final TransportValidator validator = new TransportValidator();
    private final BooleanProperty invalid = new SimpleBooleanProperty();

    public TransportImpl() {
        setInvalid(true);

        plateNo.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(newValue, marka.get(), model.get(), seats.get(),
                passportSeries.get(), passportNumber.get(), certificateSeries.get(), certificateNumber.get())));

        model.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(plateNo.get(), newValue, model.get(), seats.get(),
                passportSeries.get(), passportNumber.get(), certificateSeries.get(), certificateNumber.get())));

        marka.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(plateNo.get(), marka.get(), newValue, seats.get(),
                passportSeries.get(), passportNumber.get(), certificateSeries.get(), certificateNumber.get())));

        seats.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(plateNo.get(), marka.get(), model.get(), newValue,
                passportSeries.get(), passportNumber.get(), certificateSeries.get(), certificateNumber.get())));

        passportSeries.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(plateNo.get(), marka.get(), model.get(), seats.get(),
                newValue, passportNumber.get(), certificateSeries.get(), certificateNumber.get())));

        passportNumber.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(plateNo.get(), marka.get(), model.get(), seats.get(),
                passportSeries.get(), newValue, certificateSeries.get(), certificateNumber.get())));

        certificateSeries.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(plateNo.get(), marka.get(), model.get(), seats.get(),
                passportSeries.get(), passportNumber.get(), newValue, certificateNumber.get())));

        certificateNumber.addListener((observable, oldValue, newValue)
                -> setInvalid(isInvalid(plateNo.get(), marka.get(), model.get(), seats.get(),
                passportSeries.get(), passportNumber.get(), certificateSeries.get(), newValue)));

    }

    public static TransportImpl createOf(Transport transport) {
        TransportImpl newTransport = new TransportImpl();

        newTransport.setCertificateNumber(transport.getCertificateNumber());
        newTransport.setCertificateSeries(transport.getCertificateSeries());
        newTransport.setMarka(transport.getMarka());
        newTransport.setModel(transport.getModel());
        newTransport.setPassportNumber(transport.getPassportNumber());
        newTransport.setPassportSeries(transport.getPassportSeries());
        newTransport.setPlateNo(transport.getPlateNo());
        newTransport.setSeats(transport.getSeats());
        newTransport.setId(transport.getId());

        return newTransport;
    }

    public boolean isInvalid(String plateNo, String marka, String model, Integer seats,
                             String passportSeries, String passportNumber,
                             String certificateSeries, String certificateNumber) {
        return !validator.isValid(plateNo, marka, model, seats,
                passportSeries, passportNumber,
                certificateSeries, certificateNumber);
    }

    public void resetState() {
        setId(0);
        setCertificateNumber("");
        setCertificateSeries("");
        setMarka("");
        setModel("");
        setPassportNumber("");
        setPassportSeries("");
        setPlateNo("");
        setSeats(0);
    }

    public void copyState(Transport transport) {
        if (transport == null) {
            resetState();
        } else {
            setCertificateNumber(transport.getCertificateNumber());
            setCertificateSeries(transport.getCertificateSeries());
            setMarka(transport.getMarka());
            setModel(transport.getModel());
            setPassportNumber(transport.getPassportNumber());
            setPassportSeries(transport.getPassportSeries());
            setPlateNo(transport.getPlateNo());
            setSeats(transport.getSeats());
            setId(transport.getId());
        }
    }


    public BooleanProperty invalidProperty() {
        return invalid;
    }

    private void setInvalid(boolean invalid) {
        this.invalid.set(invalid);
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
    public String getPlateNo() {
        return plateNo.get();
    }

    public void setPlateNo(String plateNo) {
        this.plateNo.set(plateNo);
    }

    public StringProperty plateNoProperty() {
        return plateNo;
    }

    @Override
    public String getMarka() {
        return marka.get();
    }

    public void setMarka(String marka) {
        this.marka.set(marka);
    }

    public StringProperty markaProperty() {
        return marka;
    }

    @Override
    public String getModel() {
        return model.get();
    }

    public void setModel(String model) {
        this.model.set(model);
    }

    public StringProperty modelProperty() {
        return model;
    }

    @Override
    public int getSeats() {
        return seats.get();
    }

    public void setSeats(int seats) {
        this.seats.set(seats);
    }

    public ObjectProperty<Integer> seatsProperty() {
        return seats;
    }

    @Override
    public String getPassportSeries() {
        return passportSeries.get();
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries.set(passportSeries);
    }

    public StringProperty passportSeriesProperty() {
        return passportSeries;
    }

    @Override
    public String getPassportNumber() {
        return passportNumber.get();
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber.set(passportNumber);
    }

    public StringProperty passportNumberProperty() {
        return passportNumber;
    }

    @Override
    public String getCertificateSeries() {
        return certificateSeries.get();
    }

    public void setCertificateSeries(String certificateSeries) {
        this.certificateSeries.set(certificateSeries);
    }

    public StringProperty certificateSeriesProperty() {
        return certificateSeries;
    }

    @Override
    public String getCertificateNumber() {
        return certificateNumber.get();
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber.set(certificateNumber);
    }

    public StringProperty certificateNumberProperty() {
        return certificateNumber;
    }

    @Override
    public String toString() {
        return "TransportImpl{" +
                "id=" + id +
                ", plateNo=" + plateNo +
                ", marka=" + marka +
                ", model=" + model +
                ", seats=" + seats +
                ", passportSeries=" + passportSeries +
                ", passportNumber=" + passportNumber +
                ", certificateSeries=" + certificateSeries +
                ", certificateNumber=" + certificateNumber +
                ", validator=" + validator +
                ", invalid=" + invalid +
                '}';
    }
}
