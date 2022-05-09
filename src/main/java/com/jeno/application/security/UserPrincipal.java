package com.jeno.application.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.jeno.application.data.entity.AuthProviderType;

public class UserPrincipal extends User implements OAuth2User, UserDetails {

	private final AuthProviderType authProviderType;
	private final Map<String, Object> attributes;
	private final String id;

	public UserPrincipal(
			String id,
			String username,
			String password,
			boolean enabled,
			boolean accountNonExpired,
			boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,
			AuthProviderType authProviderType,
			Map<String, Object> attributes) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
		this.authProviderType = authProviderType;
		this.attributes = attributes;
	}

	public static UserPrincipal create(com.jeno.application.data.entity.User user, Map<String, Object> attributes) {
		return new UserPrincipal(
				user.getIdString(),
				user.getName(),
				user.getPassword(),
				true,
				true,
				true,
				true,
				List.of(new SimpleGrantedAuthority("ROLE_USER")),
				user.getProvider(),
				attributes
		);
	}

	public static UserPrincipal create(com.jeno.application.data.entity.User user) {
		return create(user, Map.of());
	}

	public AuthProviderType getAuthProviderType() {
		return authProviderType;
	}

	public String getId() {
		return id;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return id;
	}
}
