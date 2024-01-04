package com.tiddev.pool.client.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PoolTestResponse {
    private Long id;
    private String col1;
    private String col2;
    private String col3;
    private String col4;
    private Timestamp created;
    private Timestamp updated;

}