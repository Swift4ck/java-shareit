package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Map;

@Component
@Slf4j
public class BookingClient extends BaseClient {

    @Value("${server.url}")
    private String serverUrl;

    public BookingClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Long userId, BookingDto bookingDto) {
        log.info("Запрос на бронирования вещи от пользователя {}", userId);

        if (bookingDto.getItemId() == null) {
            throw new NotFoundException("Вещь не найдена");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || !bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new BadRequestException("Некорректное время брони");
        }

        return post(serverUrl + "/bookings", userId, bookingDto);
    }

    public ResponseEntity<Object> approved(Long userId, Long bookingId, Boolean approved) {
        log.info("Подтверждение бронирования от пользователя {}, для брони {} ", userId, bookingId);
        return patch(serverUrl + "/bookings/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        log.info("Запрос брони {} от пользователя {} ", bookingId, userId);
        return get(serverUrl + "/bookings/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBooking(Long userId, BookingState bookingState) {
        log.info("Запрос на получение информации списка бронирования пользователя {}", userId);

        Map<String, Object> state = Map.of("state", bookingState.name());

        return get(serverUrl + "/bookings", userId, state);
    }

    public ResponseEntity<Object> getOwnerBookings(Long ownerId, BookingState bookingState) {
        log.info("Запрос на получение бронирований вещей владельца {}", ownerId);

        Map<String, Object> state = Map.of("state", bookingState.name());

        return get(serverUrl + "/bookings/owner", ownerId, state);
    }

}
