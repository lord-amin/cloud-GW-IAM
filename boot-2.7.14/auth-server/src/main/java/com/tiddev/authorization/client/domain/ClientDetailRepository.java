package com.tiddev.authorization.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.awt.print.Pageable;
import java.util.List;

public interface ClientDetailRepository extends JpaRepository<ClientDetailsEntity, Long> {
    @Transactional
    @Modifying
    @Query(
            value = "truncate table oauth2_registered_client",
            nativeQuery = true
    )
    void truncateTable();

//    List<ClientDetailsEntity> findAll(Pageable pageable);

}