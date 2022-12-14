package com.alkemy.wallet.controller.user;


import com.alkemy.wallet.dto.UserUpdateDto;
import com.alkemy.wallet.exception.ForbiddenAccessException;
import com.alkemy.wallet.exception.ResourceNotFoundException;
import com.alkemy.wallet.model.Role;
import com.alkemy.wallet.model.RoleName;
import com.alkemy.wallet.model.User;
import com.alkemy.wallet.repository.RoleRepository;
import com.alkemy.wallet.repository.UserRepository;
import com.alkemy.wallet.security.JWTUtil;
import com.alkemy.wallet.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateUser {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    UserUpdateDto userUpdateDto;
    User user, user2;
    String token;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        Role userRole = new Role(1, RoleName.USER, "Users rol", new Timestamp(System.currentTimeMillis()), null);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passEncoded = passwordEncoder.encode( "test" );

        user = new User(1, "test", "test", "test@email.com", passEncoded, userRole, new Timestamp(System.currentTimeMillis()), null, false);
        user2 = new User(2, "test2", "test2", "test2@email.com", passEncoded, userRole, new Timestamp(System.currentTimeMillis()), null, false);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        when(userRepository.findByEmail(user2.getEmail())).thenReturn(user2);
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        when(userRepository.save(user2)).thenReturn(user2);

        when(userRepository.findById(3)).thenReturn(Optional.empty());

        token = jwtUtil.generateToken(user);

        userUpdateDto = new UserUpdateDto();

        userUpdateDto.setFirstName("tester");
        userUpdateDto.setLastName("tester");
        userUpdateDto.setPassword("tester");
    }

    @Test
    void correctUpdate() throws Exception {

        ResultActions resultActions = mockMvc.perform(patch("/users/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk());

    }

 
    @Test
    void userEmailUpdate() throws Exception{

        ResultActions resultActions = mockMvc.perform(patch("/users/2")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(result -> Assertions.assertInstanceOf(ForbiddenAccessException.class, result.getResolvedException()));


    }

}
