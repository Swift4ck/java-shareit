package ru.practicum.shareit.booking.servise;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto create(Long userdId, BookingDto bookingDto);

    BookingDto approved(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    Collection<BookingDto> getUserBooking(Long userId, BookingState state);

    Collection<BookingDto> getOwnerBookings(Long ownerId, BookingState state);

}
