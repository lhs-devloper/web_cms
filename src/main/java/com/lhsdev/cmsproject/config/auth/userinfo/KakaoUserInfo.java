package com.lhsdev.cmsproject.config.auth.userinfo;

import java.util.Map;

public class KakaoUserInfo extends AbstractOAuth2UserInfo {

    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null)
            return null;
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null)
            return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null)
            return null;

        return (String) profile.get("nickname");
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null)
            return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null)
            return null;

        return (String) profile.get("profile_image_url");
    }
}
