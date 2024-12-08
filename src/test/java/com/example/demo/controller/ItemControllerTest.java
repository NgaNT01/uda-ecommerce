package com.example.demo.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnItemWhenItemIdIsValid() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem()));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        verifyResponse(response, HttpStatus.OK);
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals("Created Item", item.getName());
    }

    @Test
    public void shouldReturnAllItems() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(mockItem()));

        ResponseEntity<List<Item>> response = itemController.getItems();

        verifyResponse(response, HttpStatus.OK);
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertFalse(items.isEmpty());
    }

    @Test
    public void shouldReturnItemsWhenNameMatches() {
        List<Item> itemsList = new ArrayList<>();
        itemsList.add(mockItem());
        when(itemRepository.findByName("Created Item")).thenReturn(itemsList);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Created Item");

        verifyResponse(response, HttpStatus.OK);
        List<Item> returnedItems = response.getBody();
        assertNotNull(returnedItems);
        assertEquals(itemsList, returnedItems);
    }

    private void verifyResponse(ResponseEntity<?> response, HttpStatus expectedStatus) {
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode());
    }

    private Item mockItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Created Item");
        item.setDescription("This is a mock item.");
        item.setPrice(BigDecimal.valueOf(55.0));
        return item;
    }
}
