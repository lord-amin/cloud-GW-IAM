package com.tiddev.authorization.client.controller.scopeList;

import com.tiddev.authorization.client.controller.scope.ScopeResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ScopeListResponse {
    private Long id;
    private String name;
    private List<ScopeResponse> scopes;
}
