package com.jeno.application.data.endpoint;

import javax.annotation.security.PermitAll;

import org.springframework.security.core.context.SecurityContextHolder;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

@Endpoint
@PermitAll
public class UserEndpoint {

	@Nonnull
	public boolean isLoggedIn() {
		return SecurityContextHolder.getContext().getAuthentication() != null;
	}
}
