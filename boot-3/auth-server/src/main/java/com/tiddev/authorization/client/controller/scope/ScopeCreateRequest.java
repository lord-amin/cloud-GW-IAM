package com.tiddev.authorization.client.controller.scope;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScopeCreateRequest {
    @NotEmpty(message = "The name is required.")
    @Size(min = 5, max = 50, message = "The length of name must be between 5 and 50 characters.")
    private String name;
    @NotEmpty(message = "The urlPattern is required.")
    @Size(min = 5, max = 4000, message = "The length of urlPattern must be between 5 and 4000 characters.")
    private String urlPattern;
}
