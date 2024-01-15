package com.hungdt.socket.chatsocket.oauth2;

import com.hungdt.socket.chatsocket.enums.Provider;
import com.hungdt.socket.chatsocket.enums.Status;
import com.hungdt.socket.chatsocket.model.User;
import com.hungdt.socket.chatsocket.payload.Notification;
import com.hungdt.socket.chatsocket.payload.Oauth2DTO;
import com.hungdt.socket.chatsocket.repository.UserRepository;
import com.hungdt.socket.chatsocket.security.CustomUserDetails;
import com.hungdt.socket.chatsocket.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        String oauth2ClientName = oauth2User.getOauth2ClientName();
        String username = oauth2User.getEmail();
        String name = oauth2User.getName();

        log.info(name);
        log.info(username);
        log.info(oauth2ClientName);
        Oauth2DTO dto = new Oauth2DTO(
               name, username, "default-pass", oauth2ClientName
        );

        String redirectUrl = oAuth2Login(dto, request);
        response.sendRedirect(redirectUrl);

    }

    public String oAuth2Login(Oauth2DTO oauth2DTO, HttpServletRequest req) {
        String authType = oauth2DTO.getOauthName();
        User user = new User();
        user.setStatus(Status.ONLINE);

        if(authType.equalsIgnoreCase("FACEBOOK")) {
            user.setProvider(Provider.FACEBOOK);
            Optional<User> findByFacebook = userRepository.findByProviderAndUsername(Provider.FACEBOOK, oauth2DTO.getUsername());

            if(findByFacebook.isPresent()) {
                User facebookUser = findByFacebook.get();
                facebookUser.setStatus(Status.ONLINE);
                userRepository.save(facebookUser);
                CustomUserDetails userDetails = new CustomUserDetails(facebookUser);
                setAuthenticationContext(userDetails, req);
                messagingTemplate.convertAndSend("/topic/connected", new Notification(oauth2DTO.getUsername()));
                return "/";
            }

        } else {
            user.setProvider(Provider.GOOGLE);
            Optional<User> findByGoogle = userRepository.findByProviderAndUsername(Provider.GOOGLE, oauth2DTO.getUsername());

            if(findByGoogle.isPresent()) {
                User googleUser = findByGoogle.get();
                googleUser.setStatus(Status.ONLINE);
                userRepository.save(googleUser);
                CustomUserDetails userDetails = new CustomUserDetails(googleUser);
                setAuthenticationContext(userDetails, req);
                messagingTemplate.convertAndSend("/topic/connected", new Notification(oauth2DTO.getUsername()));
                return "/";
            }
        }

        user.setUsername(oauth2DTO.getUsername());
        user.setPassword(oauth2DTO.getPassword());
        user.setName(oauth2DTO.getName());
        user.setRole("USER");
        User savedUser = userRepository.save(user);
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        setAuthenticationContext(userDetails, req);
        messagingTemplate.convertAndSend("/topic/connected", new Notification(oauth2DTO.getUsername()));
        return "/";
    }

    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest req) {
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}