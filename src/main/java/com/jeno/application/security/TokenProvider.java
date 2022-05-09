package com.jeno.application.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.jeno.application.properties.ApplicationProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class TokenProvider {

	private static final Logger LOG = LoggerFactory.getLogger(TokenProvider.class);

	private final ApplicationProperties applicationProperties;

	public TokenProvider(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public String createToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + applicationProperties.auth().tokenExpirationMsec());
		return Jwts.builder()
				.setSubject(userPrincipal.getName())
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS256, applicationProperties.auth().tokenSecret())
				.compact();
	}

	public String getUserIdFromToken(String token) {
		Claims clams = Jwts.parser()
				.setSigningKey(applicationProperties.auth().tokenSecret())
				.parseClaimsJws(token)
				.getBody();
		return clams.getSubject();
	}

	public boolean validateToken(String token) {
		if (token == null) {
			return false;
		}
		try {
			Jwts.parser().setSigningKey(applicationProperties.auth().tokenSecret()).parseClaimsJws(token);
			return true;
		} catch (SignatureException ex) {
			LOG.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			LOG.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			LOG.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			LOG.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			LOG.error("JWT claims string is empty.");
		}
		return false;
	}

}
