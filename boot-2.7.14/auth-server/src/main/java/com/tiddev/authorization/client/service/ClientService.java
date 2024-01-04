package com.tiddev.authorization.client.service;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiddev.authorization.client.domain.ClientDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientService implements InitializingBean {
    private static final String TABLE_NAME = "oauth2_registered_client";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;
    private final ClientDetailRepository clientDetailRepository;

    public int saveLocations(BufferedReader clients) throws IOException {
        long now = System.currentTimeMillis();
        log.info("truncating table ");
        clientDetailRepository.truncateTable();
        int total = 0;
        int batchSize = 5000;
        log.info("truncated table ");
        List<String> batch = new ArrayList<>();
        String line;
        while ((line = clients.readLine()) != null) {
            if (line.trim().length() == 0)
                continue;
            if (batch.size() == batchSize) {
                nativeBatch(batch);
                log.warn("inserted {} into db ", total);
                total = total + batch.size();
                batch.clear();
            }
            batch.add(line.trim());
        }
        if (!batch.isEmpty()) {
            nativeBatch(batch);
            total = total + batch.size();
            log.warn("inserted {} into db ", total);
        }
        log.warn("inserted file location into db ended {} ", (System.currentTimeMillis() - now));
        return total;
    }

    private static final String COLUMN_NAMES = "id, "
            + "client_id, "
            + "client_id_issued_at, "
            + "client_secret, "
            + "client_secret_expires_at, "
            + "client_name, "
            + "client_authentication_methods, "
            + "authorization_grant_types, "
            + "redirect_uris, "
            + "scopes, "
            + "client_settings,"
            + "token_settings";
    private static final String INSERT_REGISTERED_CLIENT_SQL = "INSERT INTO " + TABLE_NAME
            + "(" + COLUMN_NAMES + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private void nativeBatch(List<String> clients) {

        jdbcTemplate.batchUpdate(INSERT_REGISTERED_CLIENT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        List<String> clientAuthenticationMethods = List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue());

                        List<String> authorizationGrantTypes = Arrays.asList(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), AuthorizationGrantType.REFRESH_TOKEN.getValue());

                        String clientId = clients.get(i);
                        ps.setString(1, UUID.randomUUID().toString());
                        ps.setString(2, clientId);
                        ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                        ps.setString(4, clientId);
                        ps.setTimestamp(5, null);
                        ps.setString(6, clientId);
                        ps.setString(7, StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
                        ps.setString(8, StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
                        ps.setString(9, StringUtils.collectionToCommaDelimitedString(new ArrayList<>()));
                        ps.setString(10, StringUtils.collectionToCommaDelimitedString(List.of(OidcScopes.OPENID)));
                        ps.setString(11, writeMap(ClientSettings.builder().build().getSettings()));
                        ps.setString(12, writeMap(TokenSettings.builder().accessTokenTimeToLive(Duration.of(60L, ChronoUnit.SECONDS))
                                .refreshTokenTimeToLive(Duration.of(70L,ChronoUnit.SECONDS)).build().getSettings()));
                    }

                    @Override
                    public int getBatchSize() {
                        return clients.size();
                    }
                });
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public void extracted(InMemoryRegisteredClientRepository inMemoryRegisteredClientRepository) {
        long l = System.currentTimeMillis();
        int size = 10000;
        int start = 0;
        int end = start + size;
        List<RegisteredClient> all = findBy(start, end);
        int total = 0;
        while (!all.isEmpty()) {
            total = total + all.size();
            all.forEach(inMemoryRegisteredClientRepository::save);
            log.warn("The {} loaded in ram", total);
            start = start + size;
            end = end + size;
            all = findBy(start, end);
        }
        log.error("Time is {}", (System.currentTimeMillis() - l));
    }


    private List<RegisteredClient> findBy(int start, int end) {
        String sql = "SELECT " + COLUMN_NAMES + " FROM " + TABLE_NAME + " WHERE " + " rownum >= " + start + " and rownum < " + end;
        log.warn("{}", sql);
        List<RegisteredClient> result = jdbcTemplate.query(
                sql, new JdbcRegisteredClientRepository.RegisteredClientRowMapper());
        return result;
    }


    @Override
    public void afterPropertiesSet() {
        ClassLoader classLoader = JdbcRegisteredClientRepository.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }
}
