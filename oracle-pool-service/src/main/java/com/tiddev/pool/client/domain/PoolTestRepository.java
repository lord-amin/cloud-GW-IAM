package com.tiddev.pool.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface PoolTestRepository extends JpaRepository<PoolTestEntity, Long> {
    @Transactional
    @Modifying
    @Query(
            value = "truncate table tbl_pool_test",
            nativeQuery = true
    )
    void truncateTable();
}