package com.tiddev.authorization.client.controller.scopeList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScopeListCreateRequest {
    @NotEmpty(message = "The name is required.")
    @Size(min = 5, max = 50, message = "The length of name must be between 5 and 50 characters.")
    private String name;
}
