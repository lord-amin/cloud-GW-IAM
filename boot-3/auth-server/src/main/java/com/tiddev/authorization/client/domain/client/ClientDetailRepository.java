package com.tiddev.authorization.client.domain.client;

import com.tiddev.authorization.client.domain.scopeList.ScopeListEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author : Yaser(Amin) sadeghi
 */
public interface ClientDetailRepository extends JpaRepository<ClientDetailsEntity, Long> {
    @Transactional
    @Modifying
    @Query(
            value = "truncate table oauth2_registered_client_custom",
            nativeQuery = true
    )
    void truncateTable();

    @Transactional
    @Modifying
    @Query(
            value = "truncate table advanced_oauth2_scope_list_scope",
            nativeQuery = true
    )
    void truncateTableRelation();

    @Query(value = "select c.id,c.clientId,c.clientSecret,s.name,s.urlPattern,s.id,sl.id,c.accessTokenExpireSeconds,c.enable from ClientDetailsEntity c join c.scopes sl join sl.scopes s where c.clientId=:clientId")
    List<Tuple> fastFindByClientId(String clientId);


    Optional<ClientDetailsEntity> findByClientId(String clientId);
    @Query(value = "SELECT scl.pk_id,s.pk_id,s.name,s.URL_PATTERN " +
            "                 FROM" +

            "                    ADVANCED_OAUTH2_CLIENT_SCOPE_LIST             cs" +
            "                    ,ADVANCED_OAUTH2_SCOPE_LIST         scl" +
            "                    ,ADVANCED_OAUTH2_SCOPE_LIST_SCOPE   slsc" +
            "                    ,ADVANCED_OAUTH2_SCOPE                s" +
            "                 WHERE" +
            "                   cs.SCOPE_LIST_PK_ID = scl.pk_id" +
            "                   AND scl.pk_id = slsc.SCOPE_LIST_PK_ID" +
            "                   AND slsc.SCOPE_PK_ID = s.pk_id" +
            "                   AND cs.CLIENT_PK_ID= :pkId" , nativeQuery = true)
    List<Tuple> findByClientPkId(Long pkId);


}