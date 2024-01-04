package com.tiddev.authorization.client.service.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Scope {
    private Long id;
    private String name;
    private String urlPattern;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scope scope = (Scope) o;
        return id.equals(scope.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
