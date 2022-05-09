package com.jeno.application.security.local;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jeno.application.data.entity.AuthProviderType;
import com.jeno.application.data.entity.User;
import com.jeno.application.data.repository.UserRepository;
import com.jeno.application.security.UserPrincipal;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username == null) {
			throw new UsernameNotFoundException("Username was null");
		}
		Optional<User> user = userRepository.findByNameOrEmail(username, username);
		UserPrincipal userPrincipal = user
				.map(UserPrincipal::create)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username or email " + username));
		if (!AuthProviderType.LOCAL.equals(userPrincipal.getAuthProviderType())) {
			LOG.warn("Someone tried to login to the non local account " + userPrincipal);
			throw new UsernameNotFoundException("");
		}
		return userPrincipal;
	}

	@Transactional
	public UserDetails loadUserById(String id) {
		return userRepository.findById(UUID.fromString(id))
				.map(UserPrincipal::create)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id " + id));
	}

}
