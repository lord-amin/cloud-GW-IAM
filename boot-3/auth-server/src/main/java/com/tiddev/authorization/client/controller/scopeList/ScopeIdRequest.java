package com.tiddev.authorization.client.controller.scopeList;

import com.tiddev.authorization.client.controller.validator.CollectionEmpty;
import com.tiddev.authorization.client.controller.validator.CollectionNullElement;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ScopeIdRequest {
    @CollectionEmpty(message = "The scopeId is empty")
    @CollectionNullElement(message = "The scopeId[{index}] is empty")
    private Set<Long> scopeId;
}
