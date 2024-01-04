package com.tiddev.authorization.client.controller.client;

import com.tiddev.authorization.client.controller.validator.CollectionEmpty;
import com.tiddev.authorization.client.controller.validator.CollectionNullElement;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ScopeListIdRequest {
    @CollectionEmpty(message = "The scopeListId is empty")
    @CollectionNullElement(message = "The scopeListId[{index}] is empty")
    private Set<Long> scopeListId;
}
