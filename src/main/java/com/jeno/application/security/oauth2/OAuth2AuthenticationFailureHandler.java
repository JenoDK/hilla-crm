package com.jeno.application.security.oauth2;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import com.jeno.application.security.cookies.CookieUtil;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private static final Logger LOG = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		LOG.error("OAUTH2 Authentication failed", exception);

		Optional<Cookie> optionalCookie = Optional.ofNullable(request)
				.flatMap(r -> Optional.ofNullable(WebUtils.getCookie(r, CookieUtil.REDIRECT_URI_PARAM_COOKIE_NAME)));
		if (optionalCookie.isPresent()) {
			String targetUrl = UriComponentsBuilder.fromUriString(optionalCookie.get().getValue())
					.queryParam("error", exception.getLocalizedMessage())
					.build().toUriString();
			CookieUtil.removeAuthorizationCookies(request, response);
			getRedirectStrategy().sendRedirect(request, response, targetUrl);
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}

}
