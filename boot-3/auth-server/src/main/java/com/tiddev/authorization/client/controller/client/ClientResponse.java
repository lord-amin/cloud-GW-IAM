package com.tiddev.authorization.client.controller.client;

import com.tiddev.authorization.client.controller.scopeList.ScopeListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
public class ClientResponse {
    private Long id;
    private String clientId;
    private String clientName;
    private List<ScopeListResponse> scopes;
    private Boolean enable;
    private Long accessTokenExpireSeconds;
}
