package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImp;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImp itemServiceImp;

    private User owner;
    private User user;

    private Item item;
    private ItemDto itemDtoInput;

    private Comment comment;
    private CommentDto commentDtoInput;

    @BeforeEach
    public void items() {
        owner = new User(1L, "владелец", "владелец@test.com");
        user = new User(2L, "пользователь", "почта@ts.com");

        item = new Item();
        item.setId(10L);
        item.setName("Вещь");
        item.setDescription("Крутая вещь");
        item.setAvailable(true);
        item.setOtherId(owner.getId());
        item.setRequestId(null);

        itemDtoInput = new ItemDto(null, "Вещь", "Описание вещи", true, null, null);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Отличная вещь");
        comment.setItemId(10L);
        comment.setAuthorId(user.getId());
        comment.setAuthorName(user.getName());
        comment.setCreated(LocalDateTime.now());

        commentDtoInput = new CommentDto(null, "Отличная вещь", null, null, null, null);
    }

    @Test
    public void createItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item saved = invocation.getArgument(0);

            saved.setId(10L);
            return saved;
        });

        ItemDto result = itemServiceImp.createItemDto(1L, itemDtoInput);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Вещь", result.getName());
        assertEquals(owner.getId(), result.getOtherId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void createItemDtoUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImp.createItemDto(1L, itemDtoInput));

    }

    @Test
    public void updateItemValidUpdate() {
        ItemDto updateDto = new ItemDto(null, "Новое имя", "Новое описание", false, null, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemServiceImp.updateItem(owner.getId(), 10L, updateDto);

        assertEquals("Новое имя", result.getName());
        assertEquals("Новое описание", result.getDescription());

        assertFalse(result.getAvailable());

        verify(itemRepository).save(item);
    }

    @Test
    public void updateItemItemNotFound() {

        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImp.updateItem(owner.getId(), 10L, itemDtoInput));
    }

    @Test
    public void updateItemNotOwner() {

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemServiceImp.updateItem(user.getId(), 10L, itemDtoInput));

    }

    @Test
    public void updateItem() {
        ItemDto partialUpdate = new ItemDto(null, null, null, false, null, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemServiceImp.updateItem(owner.getId(), 10L, partialUpdate);

        assertEquals("Вещь", result.getName());

        assertEquals("Крутая вещь", result.getDescription());

        assertFalse(result.getAvailable());
    }

    @Test
    public void getByIdItems() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(10L)).thenReturn(Collections.emptyList());

        Booking lastBooking = new Booking();
        lastBooking.setId(100L);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(BookingStatus.APPROVED);

        Booking nextBooking = new Booking();
        nextBooking.setId(200L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setItem(item);
        nextBooking.setBooker(user);
        nextBooking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(eq(10L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(eq(10L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));

        ItemDto result = itemServiceImp.getByIdItems(owner.getId(), 10L);

        assertEquals(10L, result.getId());
        assertNotNull(result.getLastBooking());
        assertEquals(100L, result.getLastBooking().getId());
        assertNotNull(result.getNextBooking());
        assertEquals(200L, result.getNextBooking().getId());
    }

    @Test
    public void getByIdItemsNotOwner() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(10L)).thenReturn(Collections.emptyList());

        ItemDto result = itemServiceImp.getByIdItems(user.getId(), 10L);

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());

        assertTrue(result.getComments().isEmpty());
    }

    @Test
    public void getByIdItemsItemNotFound() {
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImp.getByIdItems(1L, 10L));

    }

    @Test
    public void getAllUserItems() {
        when(itemRepository.findAllByOtherId(owner.getId())).thenReturn(List.of(item));

        Collection<ItemDto> result = itemServiceImp.getAllUserItems(owner.getId());

        assertEquals(1, result.size());
        assertEquals("Вещь", result.iterator().next().getName());
    }

    @Test
    public void getAllUserItemsNoItems() {
        when(itemRepository.findAllByOtherId(owner.getId())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> itemServiceImp.getAllUserItems(owner.getId()));
    }

    @Test
    public void searchItem() {
        when(itemRepository.search("Вещь")).thenReturn(List.of(item));

        Collection<ItemDto> result = itemServiceImp.searchItem(1L, "Вещь");

        assertEquals(1, result.size());
        assertEquals("Вещь", result.iterator().next().getName());
    }

    @Test
    public void searchItemNoResults() {
        when(itemRepository.search("gg")).thenReturn(Collections.emptyList());

        Collection<ItemDto> result = itemServiceImp.searchItem(1L, "gg");

        assertTrue(result.isEmpty());
    }

    @Test
    public void addCommentValidCommentShouldReturnCommentDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(user.getId()), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemServiceImp.addComment(user.getId(), 10L, commentDtoInput);

        assertEquals(1L, result.getId());
        assertEquals("Отличная вещь", result.getText());

        assertEquals(user.getId(), result.getAuthorId());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    public void addCommentUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImp.addComment(99L, 10L, commentDtoInput));
    }

    @Test
    public void addCommenttemNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemServiceImp.addComment(user.getId(), 10L, commentDtoInput));
    }

    @Test
    public void addCommentNoCompletedBooking() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(user.getId()), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemServiceImp.addComment(user.getId(), 10L, commentDtoInput));
    }

}
