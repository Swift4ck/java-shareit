package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

@Component
public class UserClient extends BaseClient {

    @Value("${server.url}")
    private String serverUrl;

    public UserClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> createUser(UserDto user) {
        return post(serverUrl + "/users", user);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto user) {
        return patch(serverUrl + "/users/" + userId, user);
    }

    public ResponseEntity<Object> getByUserId(Long userId) {
        return get(serverUrl + "/users/" + userId);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete(serverUrl + "/users/" + userId);
    }

}
