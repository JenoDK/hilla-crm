package com.jeno.application.security.oauth2;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import com.jeno.application.properties.ApplicationProperties;
import com.jeno.application.security.TokenProvider;
import com.jeno.application.security.cookies.CookieUtil;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger LOG = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

	private final ApplicationProperties applicationProperties;
	private final TokenProvider tokenProvider;

	public OAuth2AuthenticationSuccessHandler(ApplicationProperties applicationProperties, TokenProvider tokenProvider) {
		this.applicationProperties = applicationProperties;
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		LOG.info("OAuth2 authentication succeeded for " + authentication);
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Optional<String> optionalRedirectUri = Optional.ofNullable(request)
				.flatMap(r -> Optional.ofNullable(WebUtils.getCookie(r, CookieUtil.REDIRECT_URI_PARAM_COOKIE_NAME)))
				.map(Cookie::getValue);
		if (optionalRedirectUri.isPresent() && !isAuthorizedRedirectUri(optionalRedirectUri.get())) {
			throw new AccessDeniedException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}
		String targetUrl = optionalRedirectUri.orElseGet(this::getDefaultTargetUrl);
		if (authentication != null) {
			return UriComponentsBuilder.fromUriString(targetUrl)
					.queryParam("token", tokenProvider.createToken(authentication))
					.build().toUriString();
		} else {
			return "/401";
		}
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		if (request != null) {
			super.clearAuthenticationAttributes(request);
		}
		CookieUtil.removeAuthorizationCookies(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		return applicationProperties.oauth2().authorizedRedirectUrls().stream()
				.anyMatch(authorizedUrl -> {
					URI authorizedUri = URI.create(authorizedUrl);
					return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) &&
							authorizedUri.getPort() == clientRedirectUri.getPort();
				});
	}


}
