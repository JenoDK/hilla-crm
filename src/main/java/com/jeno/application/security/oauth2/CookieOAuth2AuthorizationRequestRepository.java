package com.jeno.application.security.oauth2;

import static com.jeno.application.security.cookies.CookieUtil.COOKIE_EXPIRES_SECONDS;
import static com.jeno.application.security.cookies.CookieUtil.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;
import static com.jeno.application.security.cookies.CookieUtil.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.jeno.application.security.cookies.CookieUtil.addCookie;
import static com.jeno.application.security.cookies.CookieUtil.deserialize;
import static com.jeno.application.security.cookies.CookieUtil.removeAuthorizationCookies;
import static com.jeno.application.security.cookies.CookieUtil.serialize;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
		if (cookie == null) {
			return null;
		}
		return deserialize(cookie, OAuth2AuthorizationRequest.class);
	}

	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
		if (authorizationRequest == null) {
			removeAuthorizationCookies(request, response);
			return;
		}

		if (response != null) {
			addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serialize(authorizationRequest), COOKIE_EXPIRES_SECONDS);
			var redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
			if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
				addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, COOKIE_EXPIRES_SECONDS);
			}
		}
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		return loadAuthorizationRequest(request);
	}

}
