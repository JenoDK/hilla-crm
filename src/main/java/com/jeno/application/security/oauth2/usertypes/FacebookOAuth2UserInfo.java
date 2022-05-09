package com.jeno.application.security.oauth2.usertypes;

import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {

	public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getImageUrl() {
		if (attributes.containsKey("picture")) {
			Map pictureObj = (Map) attributes.get("picture");
			if (pictureObj != null && pictureObj.containsKey("data")) {
				Map dataObj = (Map) attributes.get("data");
				if (dataObj != null && dataObj.containsKey("url")) {
					return (String) dataObj.get("url");
				}
			}
		}
		return null;
	}
}
