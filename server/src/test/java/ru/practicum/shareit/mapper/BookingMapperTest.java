package ru.practicum.shareit.mapper;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingMapperTest {

    @Test
    public void toBookingDto() {

        User booker = new User(1L, "user", "user@edsada.ru");
        Item item = new Item(10L, "вещь", "описание", true, 2L, null);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking booking = new Booking(5L, start, end, item, booker, BookingStatus.WAITING);


        BookingDto dto = BookingMapper.toBookingDto(booking);


        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertEquals(10L, dto.getItemId());
        assertSame(item, dto.getItem());
        assertSame(booker, dto.getBooker());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
    }

    @Test
    public void toBooking() {

        User booker = new User(1L, "user", "user@edsada.ru");
        Item item = new Item(10L, "вещь", "описание", true, 3L, null);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingDto dto = new BookingDto(15L, start, end, 20L, item, booker, BookingStatus.APPROVED);


        Booking booking = BookingMapper.toBooking(dto);


        assertNotNull(booking);
        assertEquals(15L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertSame(item, booking.getItem());
        assertSame(booker, booking.getBooker());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

}
