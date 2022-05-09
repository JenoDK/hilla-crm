package com.jeno.application.properties;

import java.util.List;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "application")
@ConstructorBinding
public record ApplicationProperties(@DefaultValue OAuth2 oauth2, @DefaultValue Auth auth) {


	public record OAuth2(@DefaultValue({
			"http://localhost:8080/oauth2/redirect",
			"myandroidapp://oauth2/redirect",
			"myiosapp://oauth2/redirect"}) List<String> authorizedRedirectUrls) {}

	public record Auth(String tokenSecret, @DefaultValue("172800000") Long tokenExpirationMsec) {
		public Auth {
			Objects.requireNonNull(tokenSecret, "Please add a application.auth.tokenSecret to your properties.");
		}
	}
}
