package com.tiddev.pool.client.domain;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PoolMapper{
    PoolTestEntity create2Entity(PoolTestCreateRequest src);
    PoolTestResponse entity2Create(PoolTestEntity e);
}