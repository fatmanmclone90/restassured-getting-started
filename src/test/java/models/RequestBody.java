package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record RequestBody (String code){
}
