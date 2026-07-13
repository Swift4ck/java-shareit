package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на создания запроса вещи");
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получения запросов вещей от пользователя {}", userId);
        return itemRequestService.getAllRequestor(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех других запросов от пользователя {}", userId);
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId) {
        log.info("Запрос на получение запроса вещи с id - {}", requestId);
        return itemRequestService.getById(requestId);
    }

}
