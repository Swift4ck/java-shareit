package ru.practicum.shareit;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exception.BadRequestException;


import java.time.LocalDateTime;

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
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto createValidBookingDto() {
        return new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                10L,
                null,
                null
        );
    }

    @Test
    void createBooking() throws Exception {
        long userId = 1L;
        BookingDto enterDto = createValidBookingDto();
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("booking created");
        when(bookingClient.create(eq(userId), any(BookingDto.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("booking created"));
    }

    @Test
    void approveBooking() throws Exception {
        long userId = 1L;
        long bookingId = 5L;

        boolean approved = true;
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("approved");
        when(bookingClient.approved(userId, bookingId, approved)).thenReturn(clientResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(content().string("approved"));
    }

    @Test
    void getBookingById() throws Exception {
        long userId = 1L;
        long bookingId = 5L;

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("booking details");
        when(bookingClient.getById(userId, bookingId)).thenReturn(clientResponse);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("booking details"));
    }


    @Test
    void getUserBookings() throws Exception {
        long userId = 1L;

        BookingState state = BookingState.WAITING;
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("user");
        when(bookingClient.getUserBooking(userId, state)).thenReturn(clientResponse);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("bookingState", state.name()))
                .andExpect(status().isOk())
                .andExpect(content().string("user"));
    }

    @Test
    void getOwnerBookings() throws Exception {
        long ownerId = 1L;

        BookingState state = BookingState.FUTURE;
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("owner");
        when(bookingClient.getOwnerBookings(ownerId, state)).thenReturn(clientResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("bookingState", state.name()))
                .andExpect(status().isOk())
                .andExpect(content().string("owner"));
    }

    @Test
    void createBookingTheWrongTime() throws Exception {
        long userId = 1L;
        BookingDto invalidDto = new BookingDto(
                null,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                10L,
                null, null
        );

        when(bookingClient.create(eq(userId), any(BookingDto.class)))
                .thenThrow(new BadRequestException("Некорректное время брони"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

}
