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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jeno.application.properties.ApplicationProperties;
import com.jeno.application.security.local.CustomUserDetailsService;
import com.jeno.application.security.oauth2.CookieOAuth2AuthorizationRequestRepository;
import com.jeno.application.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.jeno.application.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.jeno.application.security.oauth2.OAuth2UserService;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

	private final OAuth2UserService oAuth2UserService;
	private final CookieOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final TokenAuthenticationFilter tokenAuthenticationFilter;
	private final CustomUserDetailsService customUserDetailsService;
	private final ApplicationProperties applicationProperties;

	public SecurityConfiguration(
			OAuth2UserService oAuth2UserService,
			CookieOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository,
			OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
			OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
			TokenAuthenticationFilter tokenAuthenticationFilter,
			CustomUserDetailsService customUserDetailsService,
			ApplicationProperties applicationProperties) {
		this.oAuth2UserService = oAuth2UserService;
		this.oAuth2AuthorizationRequestRepository = oAuth2AuthorizationRequestRepository;
		this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
		this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
		this.tokenAuthenticationFilter = tokenAuthenticationFilter;
		this.customUserDetailsService = customUserDetailsService;
		this.applicationProperties = applicationProperties;
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
//				.failureHandler(oAuth2AuthenticationFailureHandler)
//				.successHandler(oAuth2AuthenticationSuccessHandler)
//				.authorizationEndpoint()
//					.authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
//					.and()
				.userInfoEndpoint()
					.userService(oAuth2UserService);

//		http.addFilterAt(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
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
