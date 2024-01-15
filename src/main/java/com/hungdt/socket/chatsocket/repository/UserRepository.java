package com.hungdt.socket.chatsocket.repository;

import com.hungdt.socket.chatsocket.enums.Provider;
import com.hungdt.socket.chatsocket.enums.Status;
import com.hungdt.socket.chatsocket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByProviderAndUsername(Provider provider, String username);
    List<User> findAllByStatus(Status status);
}
