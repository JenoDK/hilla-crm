package com.jeno.application.data.entity;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "ApplicationUser")
public class User extends AbstractEntity {

	@NotEmpty
	private String name;

	@NotEmpty
	@Email
	private String email;

	@NotEmpty
	@JsonIgnore
	private String password;

	@NotNull
	@Enumerated(EnumType.STRING)
	private AuthProviderType provider;

	@JsonIgnore
	private String providerId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AuthProviderType getProvider() {
		return provider;
	}

	public void setProvider(AuthProviderType provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
}
