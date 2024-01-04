package com.tiddev.authorization.client.domain.scopeList;

import com.tiddev.authorization.client.domain.client.ClientDetailsEntity;
import com.tiddev.authorization.client.domain.scope.ScopeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Entity
@Table(name = "advanced_oauth2_scope_list")
@NoArgsConstructor
@Getter
@Setter
public class ScopeListEntity {
    public ScopeListEntity(Long id) {
        this.id = id;
    }

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advanced_scope_seq")
    @SequenceGenerator(sequenceName = "advanced_scope_seq", allocationSize = 1, name = "advanced_scope_seq")
    @Id
    @Column(name = "pk_id")
    private Long id;
    @Column(length = 100, name = "name")
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "advanced_oauth2_scope_list_scope",
            joinColumns = {@JoinColumn(name = "scope_list_pk_id", referencedColumnName = "pk_id")},
            inverseJoinColumns = {@JoinColumn(name = "scope_pk_id", referencedColumnName = "pk_id")})
    @BatchSize(size = 500)
    private List<ScopeEntity> scopes;
//    @ManyToMany(fetch = FetchType.LAZY,targetEntity = ClientDetailsEntity.class)
//    @JoinTable(name = "advanced_oauth2_scope_list_scope",
//            joinColumns = {@JoinColumn(name = "scope_list_pk_id", referencedColumnName = "pk_id")},
//            inverseJoinColumns = {@JoinColumn(name = "scope_pk_id", referencedColumnName = "pk_id")})
//    @BatchSize(size = 500)
//    private List<ClientDetailsEntity> clients;
}