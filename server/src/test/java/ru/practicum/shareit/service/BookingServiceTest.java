package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.servise.BookingServiceImp;
import ru.practicum.shareit.booking.servise.BookingState;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImp bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto inputDto;

    @BeforeEach
    public void users() {
        booker = new User(1L, "Фёдор Дмитриевич Игнатьев", "booker@soundcloud.com");

        owner = new User(2L, "владелец", "владелец@yandex.ru");

        item = new Item(10L, "item", "desc", true, owner.getId(), null);

        item.setAvailable(true);

        inputDto = new BookingDto(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                10L, null, null, null);

        booking = new Booking(1L,
                inputDto.getStart(), inputDto.getEnd(),
                item, booker, BookingStatus.WAITING);
    }


    @Test
    public void create() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.create(1L, inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void createUserNotFoundShouldThrowNotFoundException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, inputDto));
    }

    @Test
    public void createItemNotFoundShouldThrowNotFoundException() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, inputDto));
    }

    @Test
    public void createBookingOwnItemShouldThrowNotFoundException() {
        item.setOtherId(booker.getId());
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, inputDto));
    }

    @Test
    public void createItemNotAvailableShouldThrowBadRequestException() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.create(1L, inputDto));
    }


    @Test
    public void approve() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approved(owner.getId(), 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    public void approveValidRejectShouldReturnRejectedBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approved(owner.getId(), 1L, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    public void approveBookingNotFoundShouldThrowNotFoundException() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approved(owner.getId(), 1L, true));
    }

    @Test
    public void approveNotOwnerShouldThrowBadRequestException() {

        User otherUser = new User(3L, "other", "other@gmail.com");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.approved(otherUser.getId(), 1L, true));
    }

    @Test
    public void approveUserNotFoundShouldThrowNotFoundException() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approved(owner.getId(), 1L, true));
    }


    @Test
    public void getByIdBookerOrOwnerShouldReturnBooking() {

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(booker.getId(), 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void getByIdUserThrowNotFoundException() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(99L, 1L));
    }

    @Test
    public void getByIdBookingNotFoundShouldThrowNotFoundException() {

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(booker.getId(), 1L));
    }

    @Test
    public void getByIdNotBookerAndNotOwnerShouldThrowBadRequestException() {
        User user = new User(5L, "user", "user@eadadw.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.getById(user.getId(), 1L));
    }


    @Test
    public void getUserBookingAllStateShouldReturnAllBookingsSorted() {

        Booking booking2 = new Booking(2L,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                item, booker, BookingStatus.WAITING);

        List<Booking> bookings = List.of(booking, booking2);
        when(bookingRepository.findAllByUserId(booker.getId())).thenReturn(bookings);

        Collection<BookingDto> result = bookingService.getUserBooking(booker.getId(), BookingState.ALL);

        assertEquals(2, result.size());

        BookingDto first = result.iterator().next();
        assertEquals(2L, first.getId());
    }

    @Test
    public void getUserBookingWaitingStateShouldReturnWaitingBookings() {
        when(bookingRepository.findByUserIdAndStateWaiting(booker.getId()))
                .thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.getUserBooking(booker.getId(), BookingState.WAITING);

        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, result.iterator().next().getStatus());
    }

    @Test
    public void getUserBookingCurrentStateShouldReturnCurrentBookings() {
        when(bookingRepository.findByUserIdAndStateCurrent(booker.getId()))
                .thenReturn(Collections.emptyList());
        Collection<BookingDto> result = bookingService.getUserBooking(booker.getId(), BookingState.CURRENT);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getUserBookingPastStateShouldReturnPastBookings() {

        when(bookingRepository.findByUserIdAndStatePast(booker.getId()))
                .thenReturn(Collections.emptyList());
        Collection<BookingDto> result = bookingService.getUserBooking(booker.getId(), BookingState.PAST);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getUserBookingFutureStateShouldReturnFutureBookings() {

        when(bookingRepository.findByUserIdAndStateFuture(booker.getId()))
                .thenReturn(Collections.emptyList());
        Collection<BookingDto> result = bookingService.getUserBooking(booker.getId(), BookingState.FUTURE);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getUserBookingRejectedStateShouldReturnRejectedBookings() {

        when(bookingRepository.findByUserIdAndStateRejected(booker.getId()))
                .thenReturn(Collections.emptyList());
        Collection<BookingDto> result = bookingService.getUserBooking(booker.getId(), BookingState.REJECTED);
        assertTrue(result.isEmpty());
    }


    @Test
    public void getOwnerBookingsAllStateShouldReturnAllOwnerBookings() {

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertEquals(1, result.size());
    }

    @Test
    public void getOwnerBookingsUserNotFoundShouldThrowNotFoundException() {

        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings(owner.getId(), BookingState.ALL));
    }

    @Test
    public void getOwnerBookingsWaitingStateShouldReturnWaitingBookings() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStateWaiting(owner.getId())).thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.WAITING);

        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, result.iterator().next().getStatus());
    }

    @Test
    public void getOwnerBookingsRejectedStateShouldReturnRejectedBookings() {

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStateRejected(owner.getId())).thenReturn(Collections.emptyList());

        Collection<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), BookingState.REJECTED);

        assertTrue(result.isEmpty());
    }

    @Test
    public void getOwnerBookingsCurrentStateShouldReturnCurrentBookings() {

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStateCurrent(owner.getId())).thenReturn(Collections.emptyList());

        assertTrue(bookingService.getOwnerBookings(owner.getId(), BookingState.CURRENT).isEmpty());
    }

    @Test
    public void getOwnerBookingsFutureStateShouldReturnFutureBookings() {

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStateFuture(owner.getId())).thenReturn(Collections.emptyList());

        assertTrue(bookingService.getOwnerBookings(owner.getId(), BookingState.FUTURE).isEmpty());
    }

    @Test
    public void getOwnerBookingsPastStateShouldReturnPastBookings() {

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerIdAndStatePast(owner.getId())).thenReturn(Collections.emptyList());

        assertTrue(bookingService.getOwnerBookings(owner.getId(), BookingState.PAST).isEmpty());
    }
}