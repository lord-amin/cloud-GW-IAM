package com.tiddev.authorization.client.domain.scope;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "advanced_oauth2_scope")
@NoArgsConstructor
@Getter
@Setter
public class ScopeEntity {
    public ScopeEntity(Long id) {
        this.id = id;
    }

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advanced_scope_seq")
    @SequenceGenerator(sequenceName = "advanced_scope_seq", allocationSize = 1, name = "advanced_scope_seq")
    @Id
    @Column(name = "pk_id")
    private Long id;
    @Column(length = 100, name = "name")
    private String name;
    @Column(length = 100, name = "url_pattern")
    private String urlPattern;
//    @ManyToMany(mappedBy = "scopes")
//    @Fetch(FetchMode.JOIN)
//    @ManyToOne
//    @JoinColumn(name = "scope_list_id",nullable = false)
//    private ScopeListEntity scopeList;
}