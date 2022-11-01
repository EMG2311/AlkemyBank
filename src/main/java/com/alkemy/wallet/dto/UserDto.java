package com.alkemy.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        @JsonProperty( "name" )
        String name,
        @JsonProperty( "lastName" )
        String lastName,
        @JsonProperty( "email" )
        String email,
        @JsonProperty( "lastName" )
        String password
) {
}
