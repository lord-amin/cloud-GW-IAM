package com.tiddev.authorization.client.domain.scopeList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Yaser(Amin) sadeghi
 */
public interface ScopeListRepository extends JpaRepository<ScopeListEntity, Long> {
    @Transactional
    @Modifying
    @Query(
            value = "truncate table advanced_oauth2_scope_list",
            nativeQuery = true
    )
    void truncateTable();
}