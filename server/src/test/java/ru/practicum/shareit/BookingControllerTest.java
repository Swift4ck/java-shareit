package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.servise.BookingService;
import ru.practicum.shareit.booking.servise.BookingState;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBooking() throws Exception {
        long userId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto inputDto = new BookingDto(null, start, end, 10L, null, null, null);
        BookingDto savedDto = new BookingDto(1L, start, end, 10L, null, null, BookingStatus.WAITING);

        when(bookingService.create(eq(userId), any(BookingDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.itemId").value(10L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBookingOwner() throws Exception {
        long userId = 1L;
        long bookingId = 5L;
        boolean approved = true;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto approvedDto = new BookingDto(bookingId, start, end, 10L, null, null, BookingStatus.APPROVED);

        when(bookingService.approved(eq(userId), eq(bookingId), eq(approved))).thenReturn(approvedDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById() throws Exception {
        long userId = 1L;
        long bookingId = 5L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(bookingId, start, end, 10L, null, null, BookingStatus.WAITING);

        when(bookingService.getById(userId, bookingId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getUserBookings() throws Exception {
        long userId = 1L;
        BookingState state = BookingState.WAITING;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto dto = new BookingDto(1L, start, end, 10L, null, null, BookingStatus.WAITING);

        when(bookingService.getUserBooking(userId, state)).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("bookingState", state.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getUserBookingsReturnsAllList() throws Exception {
        long userId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto dto = new BookingDto(1L, start, end, 10L, null, null, BookingStatus.WAITING);

        when(bookingService.getUserBooking(userId, BookingState.ALL)).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getOwnerBookings() throws Exception {
        long ownerId = 1L;
        BookingState state = BookingState.FUTURE;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto dto = new BookingDto(2L, start, end, 20L, null, null, BookingStatus.WAITING);

        when(bookingService.getOwnerBookings(ownerId, state)).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("bookingState", state.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].itemId").value(20L));
    }

    @Test
    void getOwnerBookingsReturnsAllList() throws Exception {
        long ownerId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto dto = new BookingDto(2L, start, end, 20L, null, null, BookingStatus.WAITING);

        when(bookingService.getOwnerBookings(ownerId, BookingState.ALL)).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value(20L));
    }


}
