package com.hungdt.socket.chatsocket.service;

import com.hungdt.socket.chatsocket.enums.Provider;
import com.hungdt.socket.chatsocket.enums.Status;
import com.hungdt.socket.chatsocket.model.User;
import com.hungdt.socket.chatsocket.payload.ApiResponse;
import com.hungdt.socket.chatsocket.payload.LoginRequest;
import com.hungdt.socket.chatsocket.payload.Oauth2DTO;
import com.hungdt.socket.chatsocket.payload.RegisterRequest;
import com.hungdt.socket.chatsocket.repository.UserRepository;
import com.hungdt.socket.chatsocket.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;


    private User setUser(RegisterRequest req) {
        User user = new User();
        user.setName(req.getName());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setProvider(Provider.LOCAL);
        user.setRole("USER");
        user.setStatus(Status.OFFLINE);
        return user;
    }
    public ApiResponse register(RegisterRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    return new ApiResponse(
                            false, "Conflict", "Username already exists"
                    );
                })
                .orElseGet(() -> {
                    User newUser = setUser(request);
                    userRepository.save(newUser);
                    return new ApiResponse(
                            true, "Created user account", ""
                    );
                });
    }



    public List<User> findAllOnlineUser() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


    public String oAuth2Login(Oauth2DTO oauth2DTO) {
        String authType = oauth2DTO.getOauthName();
        User user = new User();

        if(authType.equalsIgnoreCase("FACEBOOK")) {
            user.setProvider(Provider.FACEBOOK);
            Optional<User> findByFacebook = userRepository.findByProviderAndUsername(Provider.FACEBOOK, oauth2DTO.getUsername());

            if(findByFacebook.isPresent()) {
                return "redirect:/";
            }

        } else {
            user.setProvider(Provider.GOOGLE);
            Optional<User> findByGoogle = userRepository.findByProviderAndUsername(Provider.GOOGLE, user.getUsername());

            if(findByGoogle.isPresent()) {
                return "redirect:/";
            }
        }

        user.setStatus(Status.OFFLINE);
        user.setUsername(oauth2DTO.getUsername());
        user.setPassword(oauth2DTO.getPassword());
        user.setName(oauth2DTO.getName());
        user.setRole("USER");
        userRepository.save(user);
        return "redirect:/";
    }




}
