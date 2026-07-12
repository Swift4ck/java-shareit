package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.UserDto;

@Component
public class ItemClient extends BaseClient {

    @Value("${server.url}")
    private String serverUrl;

    public ItemClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post(serverUrl + "/items", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch(serverUrl + "/items/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getByIdItems(Long userId, Long itemId) {
        return get(serverUrl + "/items/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllUserItems(Long userId) {
        return get(serverUrl + "/items", userId);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text) {
        return get(serverUrl + "/items/search?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post(serverUrl + "/items/" + itemId + "/comment", userId, commentDto);
    }

}
