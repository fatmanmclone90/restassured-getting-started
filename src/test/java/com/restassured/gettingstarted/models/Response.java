package com.restassured.gettingstarted.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


// fails to serialize without json property attribute
@JsonSerialize
public record Response (@JsonProperty("status")String status, @JsonProperty("type") int type) {
}
