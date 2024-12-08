package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository users;

    @Mock
    private ItemRepository items;

    @Mock
    private CartRepository cartRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addToCartNoUserError() {
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertResponseError(responseEntity, 404);
    }

    @Test
    public void addToCartNoItemError() {
        setupUserMock("Username");
        setupItemMock(1L, Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyCartRequest("Username", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        verify(items, times(1)).findById(1L);
        assertResponseError(responseEntity, 404);
    }

    @Test
    public void addToCartTest() {
        User user = setupUserMock("Username");
        Item item = setupItemMock(1L, Optional.of(createItem()));

        ModifyCartRequest modifyCartRequest = createModifyCartRequest("Username", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart responseCart = responseEntity.getBody();
        assertNotNull(responseCart);
        assertNotNull(responseCart.getItems());
        verify(cartRepository, times(1)).save(responseCart);
    }

    @Test
    public void removeFromCartNoUserError() {
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertResponseError(responseEntity, 404);
    }

    @Test
    public void removeFromCartNoItemError() {
        setupUserMock("Username");
        setupItemMock(1L, Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyCartRequest("Username", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        verify(items, times(1)).findById(1L);
        assertResponseError(responseEntity, 404);
    }

    @Test
    public void removeFromCartTest() {
        User user = setupUserMock("Username");
        Item item = setupItemMock(1L, Optional.of(createItem()));
        Cart cart = user.getCart();
        cart.addItem(item);

        ModifyCartRequest modifyCartRequest = createModifyCartRequest("Username", 1, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart responseCart = responseEntity.getBody();
        assertNotNull(responseCart);
        assertTrue(responseCart.getItems().isEmpty());
        verify(cartRepository, times(1)).save(responseCart);
    }

    private User setupUserMock(String username) {
        User user = createUser(username);
        when(users.findByUsername(username)).thenReturn(user);
        return user;
    }

    private Item setupItemMock(long itemId, Optional<Item> itemOptional) {
        when(items.findById(itemId)).thenReturn(itemOptional);
        return itemOptional.orElse(null);
    }

    private void assertResponseError(ResponseEntity<Cart> responseEntity, int expectedStatusCode) {
        assertNotNull(responseEntity);
        assertEquals(expectedStatusCode, responseEntity.getStatusCodeValue());
    }

    public static User createUser(String username) {
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setPassword("Password");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.valueOf(0.0));
        user.setCart(cart);

        return user;
    }

    public static Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Created Item");
        item.setDescription("This is fake item.");
        item.setPrice(BigDecimal.valueOf(10.0));
        return item;
    }

    public static ModifyCartRequest createModifyCartRequest(String username, long itemId, int quantity) {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setQuantity(quantity);
        return modifyCartRequest;
    }
}

