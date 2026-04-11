package com.cambofreelance.websiteservice.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceAccess{

	@JsonProperty("account")
	private Account account;
}