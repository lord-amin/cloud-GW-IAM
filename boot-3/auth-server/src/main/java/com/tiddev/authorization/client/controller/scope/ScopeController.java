package com.tiddev.authorization.client.controller.scope;

import com.tiddev.authorization.client.domain.scope.ScopeEntity;
import com.tiddev.authorization.client.domain.scope.ScopeRepository;
import com.tiddev.authorization.client.service.cache.CacheProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

/**
 * @author : Yaser(Amin) sadeghi
 */
@RequestMapping("/scope")
@RequiredArgsConstructor
@RestController
public class ScopeController {
    private final ScopeRepository repository;
    private final CacheProvider cacheProvider;

    @Transactional
    @ResponseBody
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ScopeCreateRequest createRequest) {
        ScopeEntity scopeEntity = new ScopeEntity();
        scopeEntity.setName(createRequest.getName());
        scopeEntity.setUrlPattern(createRequest.getUrlPattern());
        ScopeEntity save = repository.save(scopeEntity);
        return ResponseEntity.created(URI.create("/scope/" + save.getId())).build();
    }

    @Transactional
    @ResponseBody
    @PutMapping("/{scope_id}")
    public ScopeResponse update(@PathVariable("scope_id") Long scopeId, @Valid @RequestBody ScopeCreateRequest createRequest) {
        Optional<ScopeEntity> byId = repository.findById(scopeId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        ScopeEntity scopeEntity = byId.get();
        scopeEntity.setUrlPattern(createRequest.getUrlPattern());
        scopeEntity.setName(createRequest.getName());
        ScopeEntity save = repository.save(scopeEntity);
        cacheProvider.invalidateScope(save.getId());
        return toScopeResponse(save);
    }

    @Transactional
    @ResponseBody
    @DeleteMapping("/{scope_id}")
    public ResponseEntity<?> delete(@PathVariable("scope_id") Long scopeId) {
        Optional<ScopeEntity> byId = repository.findById(scopeId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        repository.delete(byId.get());
        cacheProvider.invalidateScope(byId.get().getId());
        return ResponseEntity.noContent().build();

    }

    @Transactional
    @ResponseBody
    @GetMapping("/{scope_id}")
    public ScopeResponse get(@PathVariable("scope_id") Long scopeId) {
        Optional<ScopeEntity> byId = repository.findById(scopeId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        ScopeEntity save = byId.get();
        return toScopeResponse(save);
    }

    @Transactional
    @ResponseBody
    @PostMapping("/batch")
    public Integer createBatch(@RequestParam("count") int count) {
        for (int i = 1; i < count + 1; i++) {
            ScopeEntity scopeEntity = new ScopeEntity();
            scopeEntity.setUrlPattern("/api/*");
            scopeEntity.setName("scope_" + i);
            repository.save(scopeEntity);
        }
        return count;
    }


    private static ScopeResponse toScopeResponse(ScopeEntity save) {
        return ScopeResponse
                .builder()
                .name(save.getName())
                .urlPattern(save.getUrlPattern())
                .id(save.getId()).build();
    }
}
