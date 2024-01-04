package com.tiddev.pool.client.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_pool_test")
@Getter
@Setter
public class PoolTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pool_test_seq")
    @SequenceGenerator(sequenceName = "pool_test_seq", allocationSize = 1, name = "pool_test_seq")
    @Column()
    private Long id;
    @Column(length = 4000, name = "column_string_1")
    private String col1;
    @Column(length = 4000, name = "column_string_2")
    private String col2;
    @Column(length = 4000, name = "column_string_3")
    private String col3;
    @Column(length = 4000, name = "column_string_4")
    private String col4;
    @Column(name = "creation_time")
    private Timestamp created;
    @Column(name = "update_time")
    private Timestamp updated;

    @PrePersist
    protected void onCreate() {
        created = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Timestamp(System.currentTimeMillis());
    }


}