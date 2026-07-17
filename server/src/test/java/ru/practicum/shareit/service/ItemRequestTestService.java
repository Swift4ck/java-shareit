package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServesImp;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemRequestTestService {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServesImp requestService;

    private User requestor;
    private User user;

    private ItemRequest itemRequest;
    private ItemRequestDto inputDto;

    private Item item;

    @BeforeEach
    public void request() {
        requestor = new User(1L, "requestor", "req@mdsa.ru");
        user = new User(2L, "user", "user@user.ru");

        itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("описание");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        inputDto = new ItemRequestDto();
        inputDto.setDescription("запрос");

        item = new Item();
        item.setId(100L);
        item.setName("вещь");
        item.setDescription("крутая");
        item.setAvailable(true);
        item.setOtherId(user.getId());
        item.setRequestId(10L);
    }

    @Test
    public void create() {

        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest saved = invocation.getArgument(0);
            saved.setId(10L);

            return saved;
        });

        ItemRequestDto result = requestService.create(requestor.getId(), inputDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("запрос", result.getDescription());
        assertNotNull(result.getCreated());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    public void createUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.create(99L, inputDto));
    }

    @Test
    public void getAllRequestor() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId()))
                .thenReturn(List.of(itemRequest));

        var result = requestService.getAllRequestor(requestor.getId());

        assertEquals(1, result.size());
        assertEquals("описание", result.iterator().next().getDescription());
    }

    @Test
    public void getAllRequestorUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getAllRequestor(99L));
    }

    @Test
    public void getAll() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestor.getId()))
                .thenReturn(List.of(itemRequest));

        var result = requestService.getAll(requestor.getId());

        assertEquals(1, result.size());
    }

    @Test
    public void getAllUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getAll(99L));
    }

    @Test
    public void getById() {
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.of(itemRequest));

        when(itemRepository.findByRequestId(10L)).thenReturn(List.of(item));

        ItemRequestDto result = requestService.getById(10L);

        assertEquals(10L, result.getId());
        assertEquals("описание", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("вещь", result.getItems().get(0).getName());
    }

    @Test
    public void getByIdRequestNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.getById(10L));
    }

    @Test
    public void getByIdNoItems() {
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(10L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = requestService.getById(10L);

        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

}
