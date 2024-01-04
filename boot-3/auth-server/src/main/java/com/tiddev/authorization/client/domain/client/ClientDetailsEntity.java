package com.tiddev.authorization.client.domain.client;

import com.tiddev.authorization.client.domain.scopeList.ScopeListEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Entity
@Table(name = "advanced_oauth2_client")
@Getter
@Setter
public class ClientDetailsEntity {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advanced_client_seq")
    @SequenceGenerator(sequenceName = "advanced_client_seq", allocationSize = 1, name = "advanced_client_seq")
    @Id
    @Column(name = "pk_id")
    private Long id;
    @Column(length = 100, name = "client_id")
    private String clientId;
    @Column(length = 200, name = "client_secret")
    private String clientSecret;
    @Column(length = 200, name = "client_name")
    private String clientName;
    @Column(length = 1000, name = "client_authentication_methods")
    private String clientAuthenticationMethods;
    @Column(length = 1000, name = "authorization_grant_types")
    private String authorizationGrantTypes;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "advanced_oauth2_client_scope_list",
            joinColumns = {@JoinColumn(name = "client_pk_id", referencedColumnName = "pk_id")},
            inverseJoinColumns = {@JoinColumn(name = "scope_list_pk_id", referencedColumnName = "pk_id")})
    @BatchSize(size = 500)
    private List<ScopeListEntity> scopes;
    @Column(length = 1000, name = "enabled")
    private Boolean enable;
    @Column(name = "token_expire_seconds")
    private Long accessTokenExpireSeconds;
    @Column(name = "refresh_expire_seconds")
    private Long refreshTokenExpireSeconds;
}