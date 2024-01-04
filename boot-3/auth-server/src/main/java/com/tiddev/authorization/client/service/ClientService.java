package com.tiddev.authorization.client.service;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiddev.authorization.client.domain.client.ClientDetailRepository;
import com.tiddev.authorization.client.domain.scopeList.ScopeListEntity;
import com.tiddev.authorization.client.domain.scopeList.ScopeListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ClientService implements InitializingBean {
    private static final String TABLE_NAME = "advanced_oauth2_client";
    private Long id =1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;
    private final ClientDetailRepository clientDetailRepository;
    private final ScopeListRepository scopeListRepository;

    public int saveLocations(BufferedReader clients) throws IOException {
        List<Long> allById = scopeListRepository.findAll().stream().map(scopeListEntity -> scopeListEntity.getId()).collect(Collectors.toList());
        if (allById.isEmpty())
            throw new RuntimeException("could not found scope list ");
        long now = System.currentTimeMillis();
        log.info("truncating table ");
        clientDetailRepository.truncateTable();
        clientDetailRepository.truncateTableRelation();
        int total = 0;
        int batchSize = 10000;
        log.info("truncated table ");
        List<String> batch = new ArrayList<>();
        String line;
        while ((line = clients.readLine()) != null) {
            if (line.trim().length() == 0)
                continue;
            if (batch.size() == batchSize) {
                nativeBatch(batch,allById);
                log.error("inserted {} into db ", total);
                total = total + batch.size();
                batch.clear();
            }
            batch.add(line.trim());
        }
        if (!batch.isEmpty()) {
            nativeBatch(batch, allById);
            total = total + batch.size();
            log.error("inserted {} into db ", total);
        }
        log.warn("inserted file location into db ended {} ", (System.currentTimeMillis() - now));
        return total;
    }

    private static final String COLUMN_NAMES = "pk_id, "
            + "client_id, "
            + "client_secret, "
            + "client_name, "
            + "client_authentication_methods, "
            + "authorization_grant_types, "
            + "enabled, "
            + "token_expire_seconds, "
            + "refresh_expire_seconds";
    private static final String INSERT_REGISTERED_CLIENT_SQL = "INSERT INTO " + TABLE_NAME
            + "(" + COLUMN_NAMES + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private void nativeBatch(List<String> clients, List<Long> scopeListId) {
        List<AbstractMap.SimpleEntry<Long, Long>> clientIsList = new ArrayList<>();
        jdbcTemplate.batchUpdate(INSERT_REGISTERED_CLIENT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        id++;
                        List<String> clientAuthenticationMethods = List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue());

                        List<String> authorizationGrantTypes = Arrays.asList(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), AuthorizationGrantType.REFRESH_TOKEN.getValue());

                        String clientId = clients.get(i);
                        ps.setLong(1,id);
                        for (Long aLong : scopeListId) {
                            clientIsList.add(new AbstractMap.SimpleEntry<>(id, aLong));
                        }
                        ps.setString(2, clientId);
                        ps.setString(3, clientId);
                        ps.setString(4, clientId);
                        ps.setString(5, StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
                        ps.setString(6, StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
                        ps.setBoolean(7, true);
                        ps.setInt(8, 5000);
                        ps.setInt(9, 6000);
                    }

                    @Override
                    public int getBatchSize() {
                        return clients.size();
                    }
                });
        jdbcTemplate.batchUpdate("INSERT INTO advanced_oauth2_client_scope_list (client_pk_id,scope_list_pk_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        ps.setLong(1, clientIsList.get(i).getKey());
                        ps.setLong(2, clientIsList.get(i).getValue());
                    }

                    @Override
                    public int getBatchSize() {
                        return clientIsList.size();
                    }
                });
    }


    @Override
    public void afterPropertiesSet() {
        ClassLoader classLoader = JdbcRegisteredClientRepository.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }
}
