package ru.practicum.shareit.booking.servise;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImp implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + userId));

        if (bookingDto.getItemId() == null) {
            throw new NotFoundException("Вещь не найдена");
        }

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена " + bookingDto.getItemId()));

        if (item.getOtherId().equals(userId)) {
            throw new NotFoundException("Свою вещь нельзя забронировать");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || !bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new BadRequestException("Некорректное время брони");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto approved(Long userId, Long bookingId, Boolean approved) {
        log.info("Подтверждение бронирования от пользователя {}, для брони {} ", userId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдена бронирование"));

        if (!booking.getItem().getOtherId().equals(userId)) {
            throw new BadRequestException("Только владелец может подтвердить");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + userId));

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        log.info("Запрос брони {} от пользователя {} ", bookingId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдена бронирование"));


        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOtherId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new BadRequestException("Только владелец вещи или тот кто бронирует имеет доступ");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getUserBooking(Long userId, BookingState state) {
        log.info("Запрос на получение информации списка бронирования пользователя {}", userId);

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByUserIdAndStateCurrent(userId);
                break;
            case PAST:
                bookings = bookingRepository.findByUserIdAndStatePast(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findByUserIdAndStateFuture(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findByUserIdAndStateWaiting(userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findByUserIdAndStateRejected(userId);
                break;
            default:
                bookings = bookingRepository.findAllByUserId(userId);
        }

        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        log.info("Запрос на получение бронирований вещей владельца {}", ownerId);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + ownerId));

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByOwnerIdAndStateCurrent(ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerIdAndStatePast(ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findByOwnerIdAndStateFuture(ownerId);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStateWaiting(ownerId);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStateRejected(ownerId);
                break;
            default: // ALL
                bookings = bookingRepository.findAllByOwnerId(ownerId);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


}
