package com.lhsdev.cmsproject.config.auth;

import com.lhsdev.cmsproject.config.auth.userinfo.*;
import com.lhsdev.cmsproject.domain.user.AuthProvider;
import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("CustomOAuth2UserService.loadUser() called");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("getAttributes : " + oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, naver, etc
        System.out.println("registrationId : " + registrationId);

        OAuth2UserInfo oAuth2UserInfo = null;

        if (registrationId.equals("google")) {
            System.out.println("Processing Google Login");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            System.out.println("Processing Naver Login");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            System.out.println("Processing Kakao Login");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("github")) {
            System.out.println("Processing Github Login");
            oAuth2UserInfo = new GithubUserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("apple")) {
            System.out.println("Processing Apple Login");
            oAuth2UserInfo = new AppleUserInfo(oAuth2User.getAttributes());
        } else {
            System.out.println("We haven't supported this provider yet.");
        }

        if (oAuth2UserInfo == null) {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        String provider = oAuth2UserInfo.getProvider();
        AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase()); // Enum conversion
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        String imageUrl = oAuth2UserInfo.getImageUrl();

        if (email == null || email.isEmpty()) {
            email = String.format("%s_%s@social.login", provider, providerId);
            System.out.println("Email is missing. Generated fallback email: " + email);
        }

        Optional<User> userOptional = userRepository.findByProviderAndProviderId(authProvider, providerId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.update(name, imageUrl); // Keep info updated (Fixed: email -> imageUrl)
            userRepository.save(user);
        } else {
            // Generate a random secure password for social users
            String randomPassword = java.util.UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(randomPassword);

            user = User.builder()
                    .name(name != null ? name : "User_" + providerId)
                    .email(email)
                    .password(encodedPassword)
                    .role(Role.USER)
                    .provider(authProvider)
                    .providerId(providerId)
                    .picture(imageUrl)
                    .build();
            try {
                userRepository.save(user);
            } catch (Exception e) {
                System.out.println("Error saving user: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
