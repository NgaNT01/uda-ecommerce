package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSubmitOrderWhenUserIsValid() {
        User user = createMockUser();
        Item item = createMockItem();
        Cart cart = user.getCart();
        cart.addItem(item);

        when(userRepository.findByUsername("Username")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("Username");

        assertResponse(response, HttpStatus.OK);
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertNotNull(order.getItems());
        assertEquals(1, order.getItems().size());
        assertEquals(item.getName(), order.getItems().get(0).getName());
    }

    @Test
    public void shouldReturnNotFoundWhenUserDoesNotExist() {
        when(userRepository.findByUsername("Username")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("Username");

        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturnOrdersForUserWhenOrdersExist() {
        User user = createMockUser();
        Item item = createMockItem();
        Cart cart = user.getCart();
        cart.addItem(item);

        when(userRepository.findByUsername("Username")).thenReturn(user);
        orderController.submit("Username"); // Assume this creates an order

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Username");

        assertResponse(response, HttpStatus.OK);
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void shouldReturnNotFoundWhenUserIsNullForOrderHistory() {
        when(userRepository.findByUsername("Username")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Username");

        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    private void assertResponse(ResponseEntity<?> response, HttpStatus expectedStatus) {
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode());
    }

    private Item createMockItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Created Item");
        item.setDescription("A sample item for testing.");
        item.setPrice(BigDecimal.valueOf(55.0));
        return item;
    }

    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Username");
        user.setPassword("Password");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.valueOf(0.0));
        user.setCart(cart);

        return user;
    }
}
