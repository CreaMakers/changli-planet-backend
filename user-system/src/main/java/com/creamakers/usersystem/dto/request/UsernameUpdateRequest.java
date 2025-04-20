package com.creamakers.usersystem.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernameUpdateRequest {
    @JsonProperty("old_username")
    private String oldUsername;

    @JsonProperty("new_username")
    private String newUsername;
}
