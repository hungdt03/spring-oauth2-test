package com.hungdt.socket.chatsocket.config;

import com.hungdt.socket.chatsocket.enums.Status;
import com.hungdt.socket.chatsocket.model.User;
import com.hungdt.socket.chatsocket.oauth2.CustomOAuth2UserService;
import com.hungdt.socket.chatsocket.oauth2.OAuthLoginSuccessHandler;
import com.hungdt.socket.chatsocket.repository.UserRepository;
import com.hungdt.socket.chatsocket.security.CustomUserDetails;
import com.hungdt.socket.chatsocket.security.CustomUserDetailsService;
import com.hungdt.socket.chatsocket.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

@Configuration
@Slf4j
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Autowired
    private OAuthLoginSuccessHandler oauthLoginSuccessHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Bean
    public SessionRegistry sessionRegistryBean() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> {
                            request
                                    .requestMatchers("/login", "/register", "/css/**", "/js/**", "/auth/register", "/auth/login", "/oauth/**" )
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated();
                        }
                )
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint((request, response, authException) -> {
                        response.sendRedirect("/login");
                    });
                })
                .formLogin(
                        form -> {
                            form.loginPage("/login")
                                    .successHandler((request, response, authentication) -> {
                                        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
                                        userRepository.findByUsername(customUserDetails.getUsername()).ifPresent(u -> {
                                            log.info("Security config login: " + customUserDetails.getUsername());
                                            u.setStatus(Status.ONLINE);
                                            userRepository.save(u);

                                            messagingTemplate.convertAndSend("/topic/connected", u.getUsername());
                                        });

                                        response.sendRedirect("/");
                                    })
                                    .permitAll();
                        }

                )
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage("/login")
                            .userInfoEndpoint(u-> {
                                u.userService(oauthUserService);
                            })
                            .successHandler(oauthLoginSuccessHandler);
                })
                .logout(out -> {
                    out.permitAll()
                            .logoutUrl("/doLogout")
                            .logoutSuccessHandler(new SimpleUrlLogoutSuccessHandler() {
                                @Override
                                public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
                                    userRepository.findByProviderAndUsername(customUserDetails.getUser().getProvider(), customUserDetails.getUsername()).ifPresent(u -> {
                                        log.info(customUserDetails.getUser().getProvider().toString());
                                        log.info(customUserDetails.getUser().getUsername());
                                        u.setStatus(Status.OFFLINE);
                                        userRepository.save(u);

                                        messagingTemplate.convertAndSend("/topic/disconnected", u.getUsername());
                                    });

                                    response.sendRedirect("/login");
                                }
                            })
                            .permitAll();
                });

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
