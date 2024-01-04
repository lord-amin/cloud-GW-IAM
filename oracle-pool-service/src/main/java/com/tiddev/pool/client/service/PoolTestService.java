package com.tiddev.pool.client.service;

import com.tiddev.pool.client.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PoolTestService implements InitializingBean {
    long id = 1000000;
    private static final String TABLE_NAME = "tbl_pool_test";
    private final JdbcTemplate jdbcTemplate;
    private final PoolTestRepository poolTestRepository;
    private final PoolMapper poolMapper;

    @Transactional
    public PoolTestResponse insert(PoolTestCreateRequest src) {
        return poolMapper.entity2Create(poolTestRepository.save(poolMapper.create2Entity(src)));
    }

    @Transactional
    public PoolTestResponse update(Long id, PoolTestCreateRequest src) {
        Optional<PoolTestEntity> byId = poolTestRepository.findById(id);
        if (byId.isEmpty()) {
            throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!", "poolTest", id), 1);
        }
        PoolTestEntity srcEntity = byId.get();
        if (!ObjectUtils.isEmpty(srcEntity.getCol1()))
            srcEntity.setCol1(src.getCol1());
        if (!ObjectUtils.isEmpty(srcEntity.getCol2()))
            srcEntity.setCol2(src.getCol2());
        if (!ObjectUtils.isEmpty(srcEntity.getCol3()))
            srcEntity.setCol3(src.getCol3());
        if (!ObjectUtils.isEmpty(srcEntity.getCol4()))
            srcEntity.setCol4(src.getCol4());
        return poolMapper.entity2Create(poolTestRepository.save(srcEntity));
    }

    @Transactional
    public void delete(Long id) {
        poolTestRepository.deleteById(id);
    }

    @Transactional
    public PoolTestResponse get(Long id) {
        Optional<PoolTestEntity> byId = poolTestRepository.findById(id);
        if(byId.isEmpty())
            throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!", "poolTest", id), 1);
        return poolMapper.entity2Create(byId.get());
    }


    public int batchInsert(BufferedReader clients, int batchSize) throws IOException {
        long now = System.currentTimeMillis();
        log.info("truncating table ");
        poolTestRepository.truncateTable();
        int total = 0;
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
            + "column_string_1, "
            + "column_string_2, "
            + "column_string_3, "
            + "column_string_4, "
            + "creation_time,"
            + "update_time";
    private static final String INSERT_REGISTERED_CLIENT_SQL = "INSERT INTO " + TABLE_NAME
            + "(" + COLUMN_NAMES + ") VALUES (?, ?, ?, ?, ?, ?,?)";

    private void nativeBatch(List<String> rows) {

        jdbcTemplate.batchUpdate(INSERT_REGISTERED_CLIENT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        String row = rows.get(i);
                        ps.setLong(1, id++);
                        ps.setString(2, row);
                        ps.setString(3, row);
                        ps.setString(4, row);
                        ps.setString(5, row);
                        ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                        ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                    }

                    @Override
                    public int getBatchSize() {
                        return rows.size();
                    }
                });
    }

    @Override
    public void afterPropertiesSet() {

    }
}
