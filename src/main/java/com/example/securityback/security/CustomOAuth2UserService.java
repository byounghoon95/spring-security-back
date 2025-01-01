package com.example.securityback.security;

import com.example.securityback.dto.auth.CustomOAuth2User;
import com.example.securityback.dto.auth.OAuth2Response;
import com.example.securityback.dto.auth.UserDto;
import com.example.securityback.entity.UserEntity;
import com.example.securityback.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2ResponseFactory oAuth2ResponseFactory;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = oAuth2ResponseFactory.createResponse(registrationId, oAuth2User.getAttributes());

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        UserEntity user = userRepository.findByUsername(username)
                .map(existingUser -> {
                    existingUser.setOAuthData(oAuth2Response.getEmail());
                    return existingUser;
                })
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity(username, "", "ROLE_USER", oAuth2Response.getEmail(), oAuth2Response.getName());
                    return userRepository.save(newUser);
                });

        UserDto userDTO = new UserDto(user.getUsername(), user.getName(), user.getRole());
        return new CustomOAuth2User(userDTO);
    }
}