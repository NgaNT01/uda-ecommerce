package com.example.demo.controller;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateUserSuccessfully() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Username");
        userRequest.setPassword("Password");
        userRequest.setConfirmPassword("Password");

        ResponseEntity<User> response = userController.createUser(userRequest);

        assertResponse(response, HttpStatus.OK);
        User createdUser = response.getBody();
        assertNotNull(createdUser);
        assertEquals(0, createdUser.getId());
        assertEquals("Username", createdUser.getUsername());
    }

    @Test
    public void shouldReturnUserWhenFoundById() {
        User user = createMockUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);

        assertResponse(response, HttpStatus.OK);
        assertEquals(user, response.getBody());
    }

    @Test
    public void shouldReturnNotFoundWhenUserIdDoesNotExist() {
        ResponseEntity<User> response = userController.findById(1L);
        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturnUserWhenFoundByUsername() {
        User user = createMockUser();
        when(userRepository.findByUsername("Username")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("Username");

        assertResponse(response, HttpStatus.OK);
        assertEquals(user, response.getBody());
        assertEquals("Username", user.getUsername());
        assertEquals("Password", user.getPassword());
    }

    @Test
    public void shouldReturnNotFoundWhenUsernameDoesNotExist() {
        ResponseEntity<User> response = userController.findByUserName("Username");

        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    private void assertResponse(ResponseEntity<?> response, HttpStatus expectedStatus) {
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode());
    }

    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Username");
        user.setPassword("Password");
        return user;
    }
}
