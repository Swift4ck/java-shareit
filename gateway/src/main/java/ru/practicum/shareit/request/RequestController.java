package ru.practicum.shareit.request;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/requests")
@RestController
@Slf4j
public class RequestController {

    private final RequestClient requestClient;

    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на создания запроса вещи");
        return requestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получения запросов вещей от пользователя {}", userId);
        return requestClient.getAllRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех других запросов от пользователя {}", userId);
        return requestClient.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId) {
        log.info("Запрос на получение запроса вещи с id - {}", requestId);
        return requestClient.getById(requestId);
    }

}
