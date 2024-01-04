package com.tiddev.authorization.client.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Slf4j
public class MyJdbcRegisteredClientRepository extends JdbcRegisteredClientRepository {
    public MyJdbcRegisteredClientRepository(JdbcOperations jdbcOperations) {
        super(jdbcOperations);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.trace("find client {} ",clientId);
        RegisteredClient byClientId = super.findByClientId(clientId);
        log.trace("client {} found",clientId);
        return byClientId;
    }
}
