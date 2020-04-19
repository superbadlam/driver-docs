package ru.driverdocs.ui.validator;

public class TransportValidator {
    public boolean isValidPlateNo(String plateNo) {
        return plateNo != null && !plateNo.trim().equals("");
    }

    public boolean isValidMarka(String marka) {
        return marka != null && !marka.trim().equals("");
    }

    public boolean isValidModel(String model) {
        return model != null && !model.trim().equals("");
    }

    public boolean isValidSeats(int seats) {
        return seats > 0;
    }

    public boolean isValidPassportSeries(String series) {
        return series != null && !series.trim().equals("");
    }

    public boolean isValidPassportNumber(String number) {
        return number != null && !number.trim().equals("");
    }

    public boolean isValidCertificateSeries(String series) {
        return series != null && !series.trim().equals("");
    }

    public boolean isValidCertificateNumber(String number) {
        return number != null && !number.trim().equals("");
    }

    public boolean isValid(String plateNo, String marka, String model, int seats,
                           String passportSeries, String passportNumber,
                           String certificateSeries, String certificateNumber) {
        return isValidPlateNo(plateNo)
                && isValidMarka(marka)
                && isValidModel(model)
                && isValidSeats(seats)
                && isValidPassportSeries(passportSeries)
                && isValidPassportNumber(passportNumber)
                && isValidCertificateSeries(certificateSeries)
                && isValidCertificateNumber(certificateNumber);
    }
}
