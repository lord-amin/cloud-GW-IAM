package com.tiddev.authorization.client.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "oauth2_registered_client")
@Getter
@Setter
public class ClientDetailsEntity {
    @Id
    @Column(length = 100)
    private String id;

    @Column(length = 100, name = "client_id")
    private String client_id;
    @Column(name = "client_id_issued_at")
    private Timestamp client_id_issued_at;
    @Column(length = 200, name = "client_secret")
    private String client_secret;
    @Column(name = "client_secret_expires_at")
    private Timestamp client_secret_expires_at;
    @Column(length = 200, name = "client_name")
    private String client_name;
    @Column(length = 1000, name = "client_authentication_methods")
    private String client_authentication_methods;
    @Column(length = 1000, name = "authorization_grant_types")
    private String authorization_grant_types;
    @Column(length = 1000, name = "redirect_uris")
    private String redirect_uris;
    @Column(length = 1000, name = "scopes")
    private String scopes;
    @Column(length = 2000, name = "client_settings")
    private String client_setting;
    @Column(length = 2000, name = "token_settings")
    private String token_setting;
}