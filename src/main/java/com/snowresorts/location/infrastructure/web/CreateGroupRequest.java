package com.snowresorts.location.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/** Request body for creating a group. {@code resortId} is optional. */
public record CreateGroupRequest(
        @NotBlank(message = "name is required") String name,
        UUID resortId) {
}
