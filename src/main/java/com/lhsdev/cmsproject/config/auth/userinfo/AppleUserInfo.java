package com.lhsdev.cmsproject.config.auth.userinfo;

import java.util.Map;

public class AppleUserInfo extends AbstractOAuth2UserInfo {

    public AppleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        // Apple name is often sent only on first login in a separate JSON,
        // standard ID token might not contain it if scopes aren't set perfectly.
        // Returning email as fallback or null.
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
