package com.alkemy.wallet.controller.authentication;

import com.alkemy.wallet.dto.UserRequestDto;
import com.alkemy.wallet.exception.ForbiddenAccessException;
import com.alkemy.wallet.mapper.AccountMapper;
import com.alkemy.wallet.mapper.TransactionMapper;
import com.alkemy.wallet.model.Role;
import com.alkemy.wallet.model.RoleName;
import com.alkemy.wallet.model.User;
import com.alkemy.wallet.repository.AccountRepository;
import com.alkemy.wallet.repository.RoleRepository;
import com.alkemy.wallet.repository.TransactionRepository;
import com.alkemy.wallet.repository.UserRepository;
import com.alkemy.wallet.security.AuthenticationRequest;
import com.alkemy.wallet.security.JWTUtil;
import com.alkemy.wallet.service.AccountService;
import com.alkemy.wallet.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class login {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @MockBean
    private RoleRepository roleRepository;

    User user;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        Role userRole = new Role(1, RoleName.USER, "Users rol", new Timestamp(System.currentTimeMillis()), null);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passEncoded = passwordEncoder.encode( "test" );

        user = new User(1, "test", "test", "test@email.com", passEncoded, userRole, new Timestamp(System.currentTimeMillis()), null, false);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    }

    @Test
    void correctLogin() throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@email.com","test");

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk());
        ;
    }

    @Test
    void incorrectLoginMailAndPass() throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("a","a");

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized());
        ;
    }

    @Test
    void incorrectLoginNull() throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(null,null);

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest());
        ;
    }

}
