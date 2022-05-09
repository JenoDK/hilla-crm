package com.jeno.application.security.cookies;

import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.util.SerializationUtils;

public class CookieUtil {

	private CookieUtil() {}

	public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
	public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
	public static final int COOKIE_EXPIRES_SECONDS = 180;

	public static String serialize(Object obj) {
		return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
	}

	public static <T> T deserialize(Cookie cookie, Class<T> clazz) {
		return clazz.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
	}

	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		var cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, @NotNull String name) {
		var cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	public static void removeAuthorizationCookies(HttpServletRequest request, HttpServletResponse response) {
		if (request != null && response != null) {
			deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
			deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
		}
	}
}
