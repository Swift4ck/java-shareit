package ru.practicum.shareit.request;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.NotFoundException;

@Component
public class RequestClient extends BaseClient {

    @Value("${server.url}")
    private String serverUrl;

    public RequestClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestDto itemRequestDto) {

        if (itemRequestDto.getDescription().isEmpty()) {
            throw new NotFoundException("Описание вещи не должно быть пустым");
        }

        return post(serverUrl + "/requests", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllRequestor(Long userId) {
        return get(serverUrl + "/requests", userId);
    }

    public ResponseEntity<Object> getAll(Long userId) {
        return get(serverUrl + "/requests/all", userId);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get(serverUrl + "/requests/" + userId, userId);
    }

}
