package com.jeno.application.properties;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "application")
@ConstructorBinding
public record ApplicationProperties(@DefaultValue Auth auth) {

	public record Auth(String tokenSecret, @DefaultValue("172800") Long tokenExpirationSeconds) {
		public Auth {
			Objects.requireNonNull(tokenSecret, "Please add a application.auth.tokenSecret to your properties.");
		}
	}
}
