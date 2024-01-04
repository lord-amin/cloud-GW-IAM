package com.tiddev.authorization.client.controller.scope;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ScopeResponse {
    private Long id;
    private String name;
    private String urlPattern;
}
