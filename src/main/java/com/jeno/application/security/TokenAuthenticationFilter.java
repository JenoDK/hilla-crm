package com.jeno.application.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jeno.application.security.local.CustomUserDetailsService;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

	private final TokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	public TokenAuthenticationFilter(
			TokenProvider tokenProvider,
			CustomUserDetailsService customUserDetailsService) {
		this.tokenProvider = tokenProvider;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);
			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				String userId = tokenProvider.getUserIdFromToken(jwt);
				UserDetails userDetails = customUserDetailsService.loadUserById(userId);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ex) {
			LOG.error("Could not set user authentication in security context", ex);
		}
		filterChain.doFilter(request, response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		} else {
			return null;
		}
	}
}
