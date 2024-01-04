package com.tiddev.authorization.client.domain.scope;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Yaser(Amin) sadeghi
 */
public interface ScopeRepository extends JpaRepository<ScopeEntity, Long> {
    @Transactional
    @Modifying
    @Query(
            value = "truncate table oauth2_scope_list",
            nativeQuery = true
    )
    void truncateTableOwner();

    @Transactional
    @Modifying
    @Query(
            value = "truncate table oauth2_scope",
            nativeQuery = true
    )
    void truncateTableRelations();


}