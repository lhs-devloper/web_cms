package com.lhsdev.cmsproject.config.auth.userinfo;

import java.util.Map;

public abstract class AbstractOAuth2UserInfo implements OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public AbstractOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
