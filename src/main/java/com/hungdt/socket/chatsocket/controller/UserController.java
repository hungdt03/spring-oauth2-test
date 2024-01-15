package com.hungdt.socket.chatsocket.controller;

import com.hungdt.socket.chatsocket.model.User;
import com.hungdt.socket.chatsocket.payload.ApiResponse;
import com.hungdt.socket.chatsocket.payload.LoginRequest;
import com.hungdt.socket.chatsocket.payload.RegisterRequest;
import com.hungdt.socket.chatsocket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }


    @GetMapping("/users/online")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllOnlineUser() {
        return userService.findAllOnlineUser();
    }
}
