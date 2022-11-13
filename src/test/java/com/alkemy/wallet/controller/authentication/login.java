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

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        List<Role> roleList = new ArrayList<Role>();

        roleList = roleRepository.findAll();


        if(roleList.size()<2) {

            Role role1 = new Role(1, RoleName.USER, "Users rol", new Timestamp(System.currentTimeMillis()), null);
            roleRepository.save(role1);

            Role role2 = new Role(2,RoleName.ADMIN, "Admins rol", new Timestamp( System.currentTimeMillis() ), null );
            roleRepository.save(role2);

        }


        Role userRole = roleRepository.findById(1).orElse(new Role(1, RoleName.USER, "Users rol", new Timestamp(System.currentTimeMillis()), null));

        User user = new User(1, "test", "test", "test@email.com", "test", userRole, new Timestamp(System.currentTimeMillis()), null, false);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    }

    @Test
    @Order(1)
    void correctLogin() throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@email.com","test");

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk());
        ;
    }

    @Test
    @Order(2)
    void incorrectLoginMail() throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("a","a");

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized());
        ;
    }

}
