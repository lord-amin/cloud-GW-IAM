package com.tiddev.authorization.client.controller.scopeList;

import com.tiddev.authorization.client.controller.scope.ScopeResponse;
import com.tiddev.authorization.client.domain.scope.ScopeEntity;
import com.tiddev.authorization.client.domain.scope.ScopeRepository;
import com.tiddev.authorization.client.domain.scopeList.ScopeListEntity;
import com.tiddev.authorization.client.domain.scopeList.ScopeListRepository;
import com.tiddev.authorization.client.service.cache.CacheProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author : Yaser(Amin) sadeghi
 */
@RequestMapping("/scope/list")
@RequiredArgsConstructor
@RestController
public class ScopeListController {
    private final ScopeListRepository repository;
    private final ScopeRepository scopeRepository;
    private final CacheProvider cacheProvider;

    @Transactional
    @ResponseBody
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ScopeListCreateRequest createRequest) {
        ScopeListEntity scopeListEntity = new ScopeListEntity();
        scopeListEntity.setName(createRequest.getName());
        ScopeListEntity save = repository.save(scopeListEntity);
        return ResponseEntity.created(URI.create("/scope/list/" + save.getId())).build();
    }

    @Transactional
    @ResponseBody
    @PutMapping("/{scope_list_id}")
    public ScopeListResponse update(@PathVariable("scope_list_id") Long scopeListId, @Valid @RequestBody ScopeListCreateRequest createRequest) {
        Optional<ScopeListEntity> byId = repository.findById(scopeListId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        ScopeListEntity scopeListEntity = byId.get();
        scopeListEntity.setName(createRequest.getName());
        ScopeListEntity save = repository.save(scopeListEntity);
        cacheProvider.invalidateScopeList(save.getId());
        return toScopeListResponse(save);
    }

    @Transactional
    @ResponseBody
    @DeleteMapping("/{scope_list_id}")
    public ResponseEntity<?> delete(@PathVariable("scope_list_id") Long scopeListId) {
        Optional<ScopeListEntity> byId = repository.findById(scopeListId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        repository.delete(byId.get());
        cacheProvider.invalidateScopeList(byId.get().getId());
        return ResponseEntity.noContent().build();

    }

    @Transactional
    @ResponseBody
    @GetMapping("/{scope_list_id}")
    public ScopeListResponse get(@PathVariable("scope_list_id") Long scopeListId) {
        Optional<ScopeListEntity> byId = repository.findById(scopeListId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        ScopeListEntity save = byId.get();
        return toScopeListResponse(save);
    }

    @Transactional
    @ResponseBody
    @PatchMapping("/{scope_list_id}")
    public ScopeListResponse assign(@PathVariable("scope_list_id") Long scopeListId, @Valid @RequestBody ScopeIdRequest scopeIdRequest) {
        Optional<ScopeListEntity> scopeListEntity = repository.findById(scopeListId);
        if (scopeListEntity.isEmpty()) {
            throw new EntityNotFoundException();
        }
        scopeListEntity.get().setScopes(scopeIdRequest.getScopeId().stream().map(ScopeEntity::new).toList());
        ScopeListEntity save = repository.save(scopeListEntity.get());
        cacheProvider.invalidateScopeList(save.getId());
        return toScopeListResponse(save);
    }

    private static ScopeListResponse toScopeListResponse(ScopeListEntity save) {
        return ScopeListResponse
                .builder()
                .name(save.getName())
                .scopes(save
                        .getScopes()
                        .stream()
                        .map(scopeEntity -> ScopeResponse
                                .builder()
                                .name(scopeEntity.getName())
                                .id(scopeEntity.getId())
                                .urlPattern(scopeEntity.getUrlPattern())
                                .build()
                        ).toList())
                .id(save.getId()).build();
    }

    @Transactional
    @ResponseBody
    @PostMapping("/batch")
    public Integer batchCreate(@RequestParam("count") int count) {
        List<ScopeEntity> all = scopeRepository.findAll();
        for (int i = 1; i < count + 1; i++) {
            ScopeListEntity scopeListEntity = new ScopeListEntity();
            scopeListEntity.setName("sc_list_" + i);
            scopeListEntity.setScopes(all);
            ScopeListEntity save = repository.save(scopeListEntity);
        }
        return count;
    }

    @Transactional
    @ResponseBody
    @PostMapping("/batch/assign")
    public Integer assign() {
        List<ScopeEntity> all = scopeRepository.findAll();
        List<ScopeListEntity> all1 = repository.findAll();

        for (ScopeListEntity scopeListEntity : all1) {
            scopeListEntity.getScopes().addAll(all);
            repository.save(scopeListEntity);
        }
        return 5;
    }

}
