package com.mankind.matrix_wishlistservice.service;

import com.mankind.matrix_wishlistservice.exception.DuplicateWishlistItemException;
import com.mankind.matrix_wishlistservice.exception.ItemNotInWishlistException;
import com.mankind.matrix_wishlistservice.exception.UserNotFoundException;
import com.mankind.matrix_wishlistservice.model.WishlistItem;
import com.mankind.matrix_wishlistservice.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository repository;

    @InjectMocks
    private WishlistService wishlistService;

    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_ID = 100L;

    private WishlistItem createWishlistItem() {
        return new WishlistItem(
                10L,
                USER_ID,
                PRODUCT_ID,
                "iPhone 13",
                "Apple",
                new BigDecimal("999.99"),
                "https://example.com/iphone13.jpg"
        );
    }

    @Test
    void addItem_shouldSaveAndReturnItem_whenProductIsNotAlreadyInWishlist() {
        WishlistItem savedItem = createWishlistItem();

        when(repository.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.empty());
        when(repository.save(any(WishlistItem.class))).thenReturn(savedItem);

        WishlistItem result = wishlistService.addItem(
                USER_ID,
                PRODUCT_ID,
                "iPhone 13",
                "Apple",
                new BigDecimal("999.99"),
                "https://example.com/iphone13.jpg"
        );

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(PRODUCT_ID, result.getProductId());
        assertEquals("iPhone 13", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());

        verify(repository).findByUserIdAndProductId(USER_ID, PRODUCT_ID);
        verify(repository).save(any(WishlistItem.class));
    }

    @Test
    void addItem_shouldThrowDuplicateWishlistItemException_whenItemAlreadyExists() {
        when(repository.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.of(createWishlistItem()));

        DuplicateWishlistItemException exception = assertThrows(
                DuplicateWishlistItemException.class,
                () -> wishlistService.addItem(
                        USER_ID,
                        PRODUCT_ID,
                        "iPhone 13",
                        "Apple",
                        new BigDecimal("999.99"),
                        "https://example.com/iphone13.jpg"
                )
        );

        assertEquals("Item already in wishlist", exception.getMessage());
        verify(repository).findByUserIdAndProductId(USER_ID, PRODUCT_ID);
        verify(repository, never()).save(any(WishlistItem.class));
    }

    @Test
    void addItemWithIdsOnly_shouldSaveItemWithNullProductDetails() {
        WishlistItem savedItem =
                new WishlistItem(10L, USER_ID, PRODUCT_ID, null, null, null, null);

        when(repository.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.empty());
        when(repository.save(any(WishlistItem.class))).thenReturn(savedItem);

        WishlistItem result = wishlistService.addItem(USER_ID, PRODUCT_ID);

        assertEquals(USER_ID, result.getUserId());
        assertEquals(PRODUCT_ID, result.getProductId());
        assertNull(result.getName());
        assertNull(result.getBrand());
        assertNull(result.getPrice());
        assertNull(result.getImageUrl());

        verify(repository).save(any(WishlistItem.class));
    }

    @Test
    void getUserWishlist_shouldReturnItems_whenWishlistIsNotEmpty() {
        List<WishlistItem> expected = List.of(createWishlistItem());
        when(repository.findByUserId(USER_ID)).thenReturn(expected);

        List<WishlistItem> result = wishlistService.getUserWishlist(USER_ID);

        assertEquals(1, result.size());
        assertEquals(PRODUCT_ID, result.get(0).getProductId());
        verify(repository).findByUserId(USER_ID);
    }

    @Test
    void getUserWishlist_shouldThrowUserNotFoundException_whenWishlistIsEmpty() {
        when(repository.findByUserId(USER_ID)).thenReturn(List.of());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> wishlistService.getUserWishlist(USER_ID)
        );

        assertEquals(
                "User not found or no wishlist items available for user " + USER_ID,
                exception.getMessage()
        );

        verify(repository).findByUserId(USER_ID);
    }

    @Test
    void removeItem_shouldDeleteItem_whenUserAndProductExist() {
        when(repository.findByUserId(USER_ID))
                .thenReturn(List.of(createWishlistItem()));
        when(repository.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.of(createWishlistItem()));

        assertDoesNotThrow(() -> wishlistService.removeItem(USER_ID, PRODUCT_ID));

        verify(repository).findByUserId(USER_ID);
        verify(repository).findByUserIdAndProductId(USER_ID, PRODUCT_ID);
        verify(repository).deleteByUserIdAndProductId(USER_ID, PRODUCT_ID);
    }

    @Test
    void removeItem_shouldThrowUserNotFoundException_whenWishlistIsEmpty() {
        when(repository.findByUserId(USER_ID)).thenReturn(List.of());

        assertThrows(
                UserNotFoundException.class,
                () -> wishlistService.removeItem(USER_ID, PRODUCT_ID)
        );

        verify(repository).findByUserId(USER_ID);
        verify(repository, never()).findByUserIdAndProductId(anyLong(), anyLong());
        verify(repository, never()).deleteByUserIdAndProductId(anyLong(), anyLong());
    }

    @Test
    void removeItem_shouldThrowItemNotInWishlistException_whenProductDoesNotExist() {
        when(repository.findByUserId(USER_ID))
                .thenReturn(List.of(createWishlistItem()));
        when(repository.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.empty());

        ItemNotInWishlistException exception = assertThrows(
                ItemNotInWishlistException.class,
                () -> wishlistService.removeItem(USER_ID, PRODUCT_ID)
        );

        assertEquals(
                "Product " + PRODUCT_ID + " not found in wishlist for user " + USER_ID,
                exception.getMessage()
        );

        verify(repository, never()).deleteByUserIdAndProductId(anyLong(), anyLong());
    }

    @Test
    void isInWishlist_shouldReturnTrue_whenProductExists() {
        when(repository.findByUserId(USER_ID))
                .thenReturn(List.of(createWishlistItem()));
        when(repository.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.of(createWishlistItem()));

        boolean result = wishlistService.isInWishlist(USER_ID, PRODUCT_ID);

        assertTrue(result);
        verify(repository).findByUserId(USER_ID);
        verify(repository).findByUserIdAndProductId(USER_ID, PRODUCT_ID);
    }

    @Test
    void isInWishlist_shouldThrowUserNotFoundException_whenWishlistIsEmpty() {
        when(repository.findByUserId(USER_ID)).thenReturn(List.of());

        assertThrows(
                UserNotFoundException.class,
                () -> wishlistService.isInWishlist(USER_ID, PRODUCT_ID)
        );

        verify(repository, never()).findByUserIdAndProductId(anyLong(), anyLong());
    }

    @Test
    void isInWishlist_shouldThrowItemNotInWishlistException_whenProductDoesNotExist() {
        when(repository.findByUserId(USER_ID))
                .thenReturn(List.of(createWishlistItem()));
        when(repository.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ItemNotInWishlistException.class,
                () -> wishlistService.isInWishlist(USER_ID, PRODUCT_ID)
        );
    }
}