package com.hungdt.socket.chatsocket.security;

import com.hungdt.socket.chatsocket.enums.Provider;
import com.hungdt.socket.chatsocket.model.User;
import com.hungdt.socket.chatsocket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByProviderAndUsername(Provider.LOCAL, username)
                .orElseThrow(() -> new UsernameNotFoundException("User does not existed"));
        return new CustomUserDetails(user);
    }
}
