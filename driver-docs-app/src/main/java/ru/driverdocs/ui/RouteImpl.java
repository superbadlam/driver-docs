package ru.driverdocs.ui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.driverdocs.domain.Route;

public class RouteImpl {
    private LongProperty id = new SimpleLongProperty();
    private StringProperty name = new SimpleStringProperty();

    public static RouteImpl createOf(long id, String name) {
        RouteImpl r = new RouteImpl();
        r.setId(id);
        r.setName(name);
        return r;
    }

    public static RouteImpl createOf(Route route) {
        RouteImpl r = new RouteImpl();
        r.setId(route.getId());
        r.setName(route.getName());
        return r;
    }

    public long getId() {
        return id.get();
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }
}
