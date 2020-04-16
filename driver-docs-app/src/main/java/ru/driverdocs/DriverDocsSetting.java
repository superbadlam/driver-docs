package ru.driverdocs;

import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.driverdocs.rxrepositories.*;
import ru.driverdocs.rxrepositories.impl.*;

import java.io.File;
import java.net.MalformedURLException;

public class DriverDocsSetting {
    private static final String SKIN_CSS = "cfg/skin.css";
    private static Logger log = LoggerFactory.getLogger(App.class);
    private static DriverDocsSetting instance;
    private final Database db;
    private final String cssUrl;
    private final DriverRepository driverRepository;
    private final DriverLicenseRepository driverLicenseRepository;
    private final MedicalRefRepository medicalRefRepository;
    private final RouteRepository routeRepository;
    private final TransportRepository transportRepository;
    private final EmployerRepository employerRepository;
    private final EmployerLicenseRepository employerLicenseRepository;

    private DriverDocsSetting() {
        db = buildDataBase();
        cssUrl = loadCSS();

        driverRepository = new DriverRepositoryImpl(db);
        driverLicenseRepository = new DriverLicenseRepositoryImpl(db);
        medicalRefRepository = new MedicalRefRepositoryImpl(db);
        routeRepository = new RouteRepositoryImpl(db);
        transportRepository = new TransportRepositoryImpl(db);
        employerRepository = new EmployerRepositoryImpl(db);
        employerLicenseRepository = new EmployerLicenseRepositoryImpl(db);
    }

    public static DriverDocsSetting getInstance() {
        if (instance == null)
            instance = new DriverDocsSetting();
        return instance;
    }

    private Database buildDataBase() {
        Database db;
        int maxPoolSize = 1;
        String url = "jdbc:h2:./db/dd";
        String user = "sa";
        String password = "driver-docs";

        db = Database
                .nonBlocking()
                .user(user).password(password)
                .url(url).maxPoolSize(maxPoolSize)
                .build();
        return db;
    }

    private String loadCSS() {
        String skinUrl = null;
        try {
            File skinFile = new File(SKIN_CSS);
            if (skinFile.exists()) {
                skinUrl = skinFile.toURI().toURL().toString();
                log.trace("будет использован скин - {}", skinUrl);
            } else {
                log.warn("не удалось найти файл скина {}", skinFile.getAbsolutePath());
            }
        } catch (MalformedURLException e) {
            log.warn("не удалось загрузить скин", e);
        }
        return skinUrl;
    }

    Database getDatabase() {
        return db;
    }

    public RouteRepository getRouteRepository() {
        return routeRepository;
    }

    public TransportRepository getTransportRepository() {
        return transportRepository;
    }

    public EmployerRepository getEmployerRepository() {
        return employerRepository;
    }

    public EmployerLicenseRepository getEmployerLicenseRepository() {
        return employerLicenseRepository;
    }

    public String getCssUrl() {
        return cssUrl;
    }

    public DriverRepository getDriverRepository() {
        return driverRepository;
    }

    public DriverLicenseRepository getDriverLicenseRepository() {
        return driverLicenseRepository;
    }

    public MedicalRefRepository getMedicalRefRepository() {
        return medicalRefRepository;
    }

}
