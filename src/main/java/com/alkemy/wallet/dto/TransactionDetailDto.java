package com.alkemy.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TransactionDetailDto(
        @JsonProperty( "id" ) Integer id,
        @JsonProperty( "amount" ) Double amount,
        @JsonProperty( "type" ) String type,
        @JsonProperty( "description" ) String description,
        @JsonProperty( "transactionDate" ) String transactionDate
){
}
