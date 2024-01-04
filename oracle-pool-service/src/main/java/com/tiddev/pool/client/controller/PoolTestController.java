package com.tiddev.pool.client.controller;

import com.tiddev.pool.client.domain.PoolTestCreateRequest;
import com.tiddev.pool.client.domain.PoolTestResponse;
import com.tiddev.pool.client.service.PoolTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PoolTestController implements InitializingBean {
    private final PoolTestService poolTestService;
    AtomicLong create = new AtomicLong();
    AtomicLong update = new AtomicLong();
    AtomicLong read = new AtomicLong();
    AtomicLong delete = new AtomicLong();

    @PostMapping(path = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String batch(
            @RequestPart("file") MultipartFile file, @RequestParam("batch") Integer batch) throws IOException {
        return "total is " + poolTestService
                .batchInsert(new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)), ObjectUtils.isEmpty(batch) ? 5000 : batch);
    }

    @PostMapping
    public ResponseEntity<PoolTestResponse> create(
            @RequestBody PoolTestCreateRequest dto) {
        create.incrementAndGet();
        return ResponseEntity.status(HttpStatus.CREATED).body(poolTestService.insert(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id) {
        delete.incrementAndGet();
        poolTestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public PoolTestResponse update(@PathVariable("id") Long id,
                                   @RequestBody PoolTestCreateRequest dto) {
        update.incrementAndGet();
        return poolTestService.update(id, dto);
    }

    @GetMapping("/{id}")
    public PoolTestResponse read(@PathVariable("id") Long id) {
        read.incrementAndGet();
        return poolTestService.get(id);
    }

    boolean started = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            long c = 0;
            long r = 0;
            long u = 0;
            long d = 0;
            while (started) {
                try {
                    log.error("C={} , R={} , U={} , D={}", (create.get() ), (read.get() ), (update.get() ), (delete.get() ));
                    TimeUnit.SECONDS.sleep(1);
                    c = create.get();
                    r = read.get();
                    u = update.get();
                    d = delete.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    protected void finalize() throws Throwable {
        started = false;
        super.finalize();
    }
}
