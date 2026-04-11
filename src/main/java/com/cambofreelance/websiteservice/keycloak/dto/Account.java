package com.cambofreelance.websiteservice.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Account{

	@JsonProperty("roles")
	private List<String> roles;
}