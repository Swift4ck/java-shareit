package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto createItemDto(Long userId, ItemDto itemDto) {
        log.info("Запрос на создание нового предмета {} от пользователя {}", itemDto, userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOtherId(userId);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Запрос на обновление item {}", itemId);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

        if (!existingItem.getOtherId().equals(userId)) {
            throw new NotFoundException("Вещь не принадлежит вам");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getByIdItems(Long userId, Long itemId) {
        log.info("Получен запрос на получение item {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        List<CommentDto> commentDtos = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemDto.setComments(commentDtos);

        LocalDateTime now = LocalDateTime.now();

        if (item.getOtherId().equals(userId)) {
            bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, now)
                    .ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingDto(booking)));

            bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                    .ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingDto(booking)));
        } else {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }

        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        log.info("Получен запрос на получение всех item, пользователя {}", userId);

        List<Item> items = itemRepository.findAllByOtherId(userId);

        if (items.isEmpty()) {
            throw new NotFoundException("не найдены вещи пользователя " + userId);
        }

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItem(Long userId, String searchString) {
        log.info("Получен запрос на поиск item, пользователя {} с текстом {}", userId, searchString);

        if (searchString.isEmpty()) {
            log.warn("Поле строки пуста");
            return List.of();
        }

        return itemRepository.search(searchString).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Добавление коментария к item {}, от пользователя {}, с текстом {}", userId, itemId, commentDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найден"));


        boolean hasCompletedBooking = bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(
                        userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()
                );

        if (!hasCompletedBooking) {
            throw new BadRequestException("Нельзя оставить комментарий без завершённого бронирования");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItemId(itemId);
        comment.setAuthorName(user.getName());
        comment.setAuthorId(userId);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        CommentDto result = new CommentDto();
        result.setId(saved.getId());
        result.setText(saved.getText());
        result.setAuthorId(saved.getAuthorId());
        result.setCreated(saved.getCreated());
        result.setAuthorName(saved.getAuthorName());
        result.setItemId(saved.getItemId());

        return result;
    }

}




