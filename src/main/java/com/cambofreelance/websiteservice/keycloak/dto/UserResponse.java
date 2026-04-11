package com.cambofreelance.websiteservice.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserResponse{

	@JsonProperty("sub")
	private String sub;

	@JsonProperty("resource_access")
	private ResourceAccess resourceAccess;

	@JsonProperty("email_verified")
	private Boolean emailVerified;

	@JsonProperty("allowed-origins")
	private List<String> allowedOrigins;

	@JsonProperty("iss")
	private String iss;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("typ")
	private String typ;

	@JsonProperty("preferred_username")
	private String preferredUsername;

	@JsonProperty("given_name")
	private String givenName;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("client_id")
	private String clientId;

	@JsonProperty("sid")
	private String sid;

	@JsonProperty("aud")
	private String aud;

	@JsonProperty("acr")
	private String acr;

	@JsonProperty("realm_access")
	private RealmAccess realmAccess;

	@JsonProperty("azp")
	private String azp;

	@JsonProperty("scope")
	private String scope;

	@JsonProperty("name")
	private String name;

	@JsonProperty("exp")
	private Integer exp;

	@JsonProperty("iat")
	private Integer iat;

	@JsonProperty("family_name")
	private String familyName;

	@JsonProperty("jti")
	private String jti;

	@JsonProperty("email")
	private String email;

	@JsonProperty("username")
	private String username;
}