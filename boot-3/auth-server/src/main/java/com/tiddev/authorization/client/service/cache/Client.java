package com.tiddev.authorization.client.service.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//@Builder
@Getter
@Setter
public class Client {
    public Client() {
        scopeListId = new HashSet<>();
    }

    public Long id;
    public String clientId;
    public String clientSecret;
    public Long accessTokenSeconds;
    public Set<Long> scopeListId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
