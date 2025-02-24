package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			logger.info("UserController::findById - User with id {} is not found", id);
			return ResponseEntity.notFound().build();
		} else {
			logger.info("UserController::findById - Get user success with id {} - username {}", id, user.get().getUsername());
			return ResponseEntity.ok(user.get());
		}
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			logger.info("UserController::findByUsername - User with name {} is not found", username);
			return ResponseEntity.notFound().build();
		} else {
			logger.info("UserController::findByUsername - Get user success with username {}", username);
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try {
			User user = new User();
			user.setUsername(createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			user.setUsername(createUserRequest.getUsername());

			if (!isPasswordValid(createUserRequest.getPassword()) ||
					!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
				logger.info("UserController::createUser - Password is invalid, try again.");
				return ResponseEntity.badRequest().build();
			}

			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			userRepository.save(user);

			logger.info("UserController::createUser - Create user successful with username {}", user.getUsername());
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			logger.error("UserController::createUser - Error creating user with name {}", createUserRequest.getUsername());
			return null;
		}
	}

	private boolean isPasswordValid(String password) {
		return password != null && password.length() > 5;
	}
	
}
