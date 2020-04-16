package ru.driverdocs.rxrepositories.data;

import ru.driverdocs.domain.Route;

public final class RouteImpl implements Route {
    private final long id;
    private final String name;

    private RouteImpl(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RouteImpl createOf(long id, String name) {
        if (id <= 0)
            throw new IllegalArgumentException("id не может принимать значения меньше единицы");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name не может отсутствовать");
        return new RouteImpl(id, name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
