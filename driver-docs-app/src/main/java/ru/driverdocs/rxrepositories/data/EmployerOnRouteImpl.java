package ru.driverdocs.rxrepositories.data;

import ru.driverdocs.domain.EmployerOnRoute;

public final class EmployerOnRouteImpl implements EmployerOnRoute {
    private final long id;
    private final long employerId;
    private final long routeId;

    private EmployerOnRouteImpl(long id, long employerId, long routeId) {
        this.id = id;
        this.employerId = employerId;
        this.routeId = routeId;
    }

    public static EmployerOnRouteImpl createOf(long id, long employerId, long routeId) {
        if (id <= 0)
            throw new IllegalArgumentException("id не может принимать значения меньше единицы");
        if (employerId <= 0)
            throw new IllegalArgumentException("employerId не может принимать значения меньше единицы");
        if (routeId <= 0)
            throw new IllegalArgumentException("routeId не может принимать значения меньше единицы");

        return new EmployerOnRouteImpl(id, employerId, routeId);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getEmployerId() {
        return employerId;
    }

    @Override
    public long getRouteId() {
        return routeId;
    }
}
