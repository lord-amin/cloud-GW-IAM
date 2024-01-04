package com.tiddev.authorization.client.controller.client;

import com.tiddev.authorization.client.controller.exception.BusinessException;
import com.tiddev.authorization.client.controller.exception.GeneralCodes;
import com.tiddev.authorization.client.controller.scope.ScopeResponse;
import com.tiddev.authorization.client.controller.scopeList.ScopeListResponse;
import com.tiddev.authorization.client.domain.client.ClientDetailRepository;
import com.tiddev.authorization.client.domain.client.ClientDetailsEntity;
import com.tiddev.authorization.client.domain.scopeList.ScopeListEntity;
import com.tiddev.authorization.client.service.ClientService;
import com.tiddev.authorization.client.service.cache.CacheProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * @author : Yaser(Amin) sadeghi
 */
@RequestMapping("/client")
@RequiredArgsConstructor
@RestController
public class ClientController {
    private final ClientDetailRepository repository;
    private final ClientService clientService;
    private final CacheProvider cacheProvider;

    @ResponseBody
    @PostMapping
    public ClientResponse create(@Valid @RequestBody ClientCreateRequest clientRequest) {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setClientId(clientRequest.getClientId());
        clientDetailsEntity.setClientName(clientRequest.getClientName());
        clientDetailsEntity.setClientSecret(clientRequest.getClientSecret());
        clientDetailsEntity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(Collections.singletonList(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue())));
        clientDetailsEntity.setAuthorizationGrantTypes(StringUtils.
                collectionToCommaDelimitedString(Arrays.asList(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), AuthorizationGrantType.REFRESH_TOKEN.getValue())));
        clientDetailsEntity.setAccessTokenExpireSeconds(clientRequest.getTokenExpiresSeconds());
        clientDetailsEntity.setRefreshTokenExpireSeconds(clientRequest.getTokenExpiresSeconds() + 5);
        clientDetailsEntity.setEnable(true);
        ClientDetailsEntity save = repository.save(clientDetailsEntity);
        return toClientResponse(save);
    }

    @Transactional
    @ResponseBody
    @PutMapping("/{client_id}")
    public ClientResponse update(@PathVariable("client_id") Long clientId, @Valid @RequestBody ClientCreateRequest createRequest) {
        Optional<ClientDetailsEntity> byId = repository.findById(clientId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        ClientDetailsEntity clientDetailsEntity = byId.get();
        clientDetailsEntity.setClientId(createRequest.getClientId());
        clientDetailsEntity.setClientSecret(createRequest.getClientSecret());
        clientDetailsEntity.setClientName(createRequest.getClientName());
        clientDetailsEntity.setAccessTokenExpireSeconds(createRequest.getTokenExpiresSeconds());
        ClientDetailsEntity save = repository.save(clientDetailsEntity);
        cacheProvider.invalidateClient(save.getClientId());
        return toClientResponse(save);
    }

    @Transactional
    @ResponseBody
    @DeleteMapping("/{client_id}")
    public ResponseEntity<?> delete(@PathVariable("client_id") Long clientId) {
        Optional<ClientDetailsEntity> byId = repository.findById(clientId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        repository.delete(byId.get());
        cacheProvider.invalidateClient(byId.get().getClientId());
        return ResponseEntity.noContent().build();

    }

    @Transactional
    @ResponseBody
    @GetMapping("/{client_id}")
    public ClientResponse get(@PathVariable("client_id") Long clientId) {
        Optional<ClientDetailsEntity> byId = repository.findById(clientId);
        if (byId.isEmpty())
            throw new EntityNotFoundException();
        ClientDetailsEntity save = byId.get();
        return toClientResponse(save);
    }

    @Transactional
    @ResponseBody
    @PatchMapping("/{client_id}")
    public ClientResponse assign(@PathVariable("client_id") String clientId, @Valid @RequestBody ScopeListIdRequest scopeListIdRequest) {
        Optional<ClientDetailsEntity> clientDetailsEntity = repository.findByClientId(clientId);
        if (clientDetailsEntity.isEmpty()) {
            throw new EntityNotFoundException();
        }
        clientDetailsEntity.get().setScopes(scopeListIdRequest.getScopeListId().stream().map(ScopeListEntity::new).toList());
        ClientDetailsEntity save = repository.save(clientDetailsEntity.get());
        cacheProvider.invalidateClient(save.getClientId());
        return toClientResponse(save);
    }

    @Transactional
    @ResponseBody
    @PatchMapping("/{client_id}/disable")
    public ResponseEntity<?> disable(@PathVariable("client_id") String clientId) {
        return activate(clientId, false);
    }

    @Transactional
    @ResponseBody
    @PatchMapping("/{client_id}/enable")
    public ResponseEntity<?> enable(@PathVariable("client_id") String clientId) {
        return activate(clientId, true);
    }

    private ResponseEntity<?> activate(String clientId, boolean enable) {
        Optional<ClientDetailsEntity> clientDetailsEntity = repository.findByClientId(clientId);
        if (clientDetailsEntity.isEmpty()) {
            throw new BusinessException(GeneralCodes.FAILED, HttpStatus.NOT_FOUND, "The client " + clientId + " not found");
        }
        clientDetailsEntity.get().setEnable(enable);
        ClientDetailsEntity save = repository.save(clientDetailsEntity.get());
        cacheProvider.invalidateClient(save.getClientId());
        return ResponseEntity.noContent().build();
    }

    private static ClientResponse toClientResponse(ClientDetailsEntity save) {
        return ClientResponse.builder()
                .id(save.getId())
                .clientId(save.getClientId())
                .clientName(save.getClientName())
                .enable(save.getEnable())
                .accessTokenExpireSeconds(save.getAccessTokenExpireSeconds())
                .scopes(save
                        .getScopes()
                        .stream()
                        .map(scopeListEntity -> ScopeListResponse.builder()
                                .id(scopeListEntity.getId())
                                .name(scopeListEntity.getName())
                                .scopes(scopeListEntity
                                        .getScopes()
                                        .stream()
                                        .map(scopeEntity -> ScopeResponse
                                                .builder()
                                                .id(scopeEntity.getId())
                                                .name(scopeEntity.getName())
                                                .urlPattern(scopeEntity.getUrlPattern())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    @PostMapping(path = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleFileUploadUsingCurl(
            @RequestPart("file") MultipartFile file) throws IOException {
        return "total is " + clientService.saveLocations(new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)));
    }
}
