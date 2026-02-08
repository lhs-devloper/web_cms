package com.lhsdev.cmsproject.config.auth.userinfo;

import java.util.Map;

public class GithubUserInfo extends AbstractOAuth2UserInfo {

    public GithubUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getEmail() {
        // GitHub email might be private, handled slightly differently if null,
        // but for basic implementation we try to get it from attributes.
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("login"); // Using login (username) as name
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
