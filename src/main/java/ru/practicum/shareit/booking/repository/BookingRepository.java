package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    List<Booking> findAllByUserId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByUserIdAndStateCurrent(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByUserIdAndStatePast(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByUserIdAndStateFuture(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findByUserIdAndStateWaiting(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findByUserIdAndStateRejected(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.otherId = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.otherId = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStateCurrent(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.otherId = :ownerId " +
            "AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatePast(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.otherId = :ownerId " +
            "AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStateFuture(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.otherId = :ownerId " +
            "AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStateWaiting(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.otherId = :ownerId " +
            "AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStateRejected(Long ownerId);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId,
                                                           BookingStatus status, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
}
