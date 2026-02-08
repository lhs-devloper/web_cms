package com.lhsdev.cmsproject.config.auth.userinfo;

public interface OAuth2UserInfo {
    String getProviderId();

    String getProvider();

    String getEmail();

    String getName();

    String getImageUrl();
}
