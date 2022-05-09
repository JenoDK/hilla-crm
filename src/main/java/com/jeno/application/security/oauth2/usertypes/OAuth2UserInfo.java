package com.jeno.application.security.oauth2.usertypes;

import java.util.Map;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import com.jeno.application.data.entity.AuthProviderType;

public abstract class OAuth2UserInfo {

	protected final Map<String, Object> attributes;

	protected OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public static OAuth2UserInfo getUserInfo(String registrationId, Map<String, Object> attributes) {
		if (AuthProviderType.GOOGLE.toString().equalsIgnoreCase(registrationId)) {
			return new GoogleOAuth2UserInfo(attributes);
		} else if (AuthProviderType.FACEBOOK.toString().equalsIgnoreCase(registrationId)) {
			return new FacebookOAuth2UserInfo(attributes);
		} else {
			throw new OAuth2AuthenticationException("Sorry! Login with $registrationId is not supported yet.");
		}
	}

	public abstract String getId();

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();
}
