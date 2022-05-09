package com.jeno.application.security.oauth2;

import java.util.Objects;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jeno.application.data.entity.AuthProviderType;
import com.jeno.application.data.entity.User;
import com.jeno.application.data.repository.UserRepository;
import com.jeno.application.security.UserPrincipal;
import com.jeno.application.security.oauth2.usertypes.OAuth2UserInfo;

@Component
public class OAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	public OAuth2UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		var oauth2User = super.loadUser(userRequest);
		try {
			return processOAuth2User(userRequest, oauth2User);
		} catch (OAuth2AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			// Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
		if (userRequest == null) {
			throw new OAuth2AuthenticationException("OAuth2 request is null");
		}
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.getUserInfo(userRequest.getClientRegistration().getRegistrationId(), oauth2User.getAttributes());
		if (!StringUtils.hasText(oAuth2UserInfo.getEmail()) || !StringUtils.hasText(oAuth2UserInfo.getId())) {
			throw new OAuth2AuthenticationException("Insufficient info provided from OAuth2 provider ${oAuth2UserRequest.clientRegistration.registrationId}");
		}
		User user = userRepository.findByProviderId(oAuth2UserInfo.getId())
				.map(u -> updateExistingUser(u, oAuth2UserInfo))
				.orElseGet(() -> registerNewUser(userRequest, oAuth2UserInfo));
		return UserPrincipal.create(user, oauth2User.getAttributes());
	}

	private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
		User user = new User();
		user.setName(oAuth2UserInfo.getName());
		user.setPassword("passwordIsIgnored");
		user.setEmail(oAuth2UserInfo.getEmail());
		user.setProvider(AuthProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()));
		user.setProviderId(oAuth2UserInfo.getId());
		return userRepository.save(user);
	}

	private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
		if (!Objects.equals(existingUser.getName(), oAuth2UserInfo.getName())) {
			existingUser.setName(oAuth2UserInfo.getName());
		}
		return existingUser;
	}

}
