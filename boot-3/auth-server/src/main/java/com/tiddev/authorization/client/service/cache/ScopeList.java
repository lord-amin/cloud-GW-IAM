package com.tiddev.authorization.client.service.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class ScopeList {
    public ScopeList() {
        scopeId = new HashSet<>();
    }

    private Long id;
    private Set<Long> scopeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScopeList scopeList = (ScopeList) o;
        return id.equals(scopeList.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
