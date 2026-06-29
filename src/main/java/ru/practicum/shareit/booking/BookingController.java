package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.servise.BookingService;
import ru.practicum.shareit.booking.servise.BookingState;

import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Запрос на бронирования вещи от пользователя {}", userId);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approved(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("Подтверждение бронирования от пользователя " + userId + " для брони " + bookingId);
        return bookingService.approved(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Запрос для получение брони {} от пользователя {} ", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getUserBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingState bookingState) {
        log.info("Получен запрос на получение информации списка бронирования пользователя {}", userId);
        return bookingService.getUserBooking(userId, bookingState);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(defaultValue = "ALL") BookingState bookingState) {
        log.info("Получен запрос на получение бронирований вещей владельца {}", ownerId);
        return bookingService.getOwnerBookings(ownerId, bookingState);
    }

}
