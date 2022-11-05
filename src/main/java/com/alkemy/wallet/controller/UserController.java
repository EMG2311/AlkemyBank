package com.alkemy.wallet.controller;

import com.alkemy.wallet.dto.UserDetailDto;
import com.alkemy.wallet.dto.UserDto;
import com.alkemy.wallet.model.User;
import com.alkemy.wallet.service.UserService;
import com.alkemy.wallet.service.implementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping( "/users" )
class UserController {
    private final UserService userservice;

    private final UserServiceImpl userServiceImpl;

    @GetMapping
    List<UserDto> getAll() {
        return userservice.getAllUsers();
    }

    @GetMapping( value = "/{id}")
    @PreAuthorize("hasRole('USER_ROLE')")
    public ResponseEntity<UserDetailDto> getUserDetailById(@PathVariable("id") Integer id, @RequestHeader("Authorization") String token ) throws Exception {
       User user = userServiceImpl.getUser(id,token);
       return ResponseEntity.ok(userServiceImpl.getUserDetailById(user.getUserId()));
    }

}
