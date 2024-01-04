package com.tiddev.authorization.client.controller.client;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : Yaser(Amin) sadeghi
 */
@Builder()
@Getter
@Setter
public class ClientCreateRequest {
    @NotEmpty(message = "The clientId is required.")
    @Size(min = 5, max = 50, message = "The length of clientId must be between 5 and 50 characters.")
    private String clientId;
    @NotEmpty(message = "The clientSecret is required.")
    @Size(min = 5, max = 50, message = "The length of clientSecret must be between 5 and 50 characters.")
    private String clientSecret;
    @NotEmpty(message = "The clientName is required.")
    @Size(min = 5, max = 50, message = "The length of clientName must be between 5 and 50 characters.")
    private String clientName;
    @NotEmpty(message = "The tokenExpiresSeconds is required.")
    @Min(value = 60, message = "The tokenExpiresSeconds must be greater than 60.")
    @Max(value = 600, message = "The tokenExpiresSeconds must be greater than 600.")
    private Long tokenExpiresSeconds;
}
