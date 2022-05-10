package com.jeno.application.security;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.jeno.application.properties.ApplicationProperties;
import com.jeno.application.security.local.CustomUserDetailsService;
import com.jeno.application.security.oauth2.OAuth2UserService;
import com.vaadin.flow.spring.security.VaadinDefaultRequestCache;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

	private final OAuth2UserService oAuth2UserService;
	private final CustomUserDetailsService customUserDetailsService;
	private final ApplicationProperties applicationProperties;
	private final VaadinDefaultRequestCache vaadinDefaultRequestCache;

	public SecurityConfiguration(
			OAuth2UserService oAuth2UserService,
			CustomUserDetailsService customUserDetailsService,
			ApplicationProperties applicationProperties,
			VaadinDefaultRequestCache vaadinDefaultRequestCache) {
		this.oAuth2UserService = oAuth2UserService;
		this.customUserDetailsService = customUserDetailsService;
		this.applicationProperties = applicationProperties;
		this.vaadinDefaultRequestCache = vaadinDefaultRequestCache;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		setLoginView(http, "/login");

		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		setStatelessAuthentication(
				http,
				new SecretKeySpec(Base64.getDecoder().decode(applicationProperties.auth().tokenSecret()), JwsAlgorithms.HS256),
				"com.jeno.application");

		http.oauth2Login()
				.loginPage("/login")
				.userInfoEndpoint()
					.userService(oAuth2UserService).and()
				.successHandler(getRedirectSuccessHandler());
	}

	private AuthenticationSuccessHandler getRedirectSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler requestAwareAuthenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		requestAwareAuthenticationSuccessHandler.setRequestCache(vaadinDefaultRequestCache);
		return requestAwareAuthenticationSuccessHandler;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
