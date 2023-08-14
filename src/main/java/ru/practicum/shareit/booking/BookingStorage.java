package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.id = :id " +
            "ORDER BY b.startDate desc")
    @NonNull
    Optional<Booking> findById(@Param("id")
                               @NonNull Long id);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.booker = :user ")
    List<Booking> findByBooker(@Param("user") User booker, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.booker = :user " +
            "   AND b.startDate < :time " +
            "   AND b.endDate > :time ")
    List<Booking> findByBookerCurrent(
            @Param("user") User booker,
            @Param("time") LocalDateTime currentTime,
            Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.booker = :user " +
            "   AND b.endDate < :time ")
    List<Booking> findByBookerPast(@Param("user") User booker,
                                   @Param("time") LocalDateTime currentTime, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.booker = :user " +
            "   AND b.startDate > :time ")
    List<Booking> findByBookerFuture(@Param("user") User booker,
                                     @Param("time") LocalDateTime currentTime, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.booker = :user " +
            "   AND b.status = :status ")
    List<Booking> findByBookerAndStatus(@Param("user") User booker,
                                        @Param("status") Status status, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user ")
    List<Booking> findByItemOwner(@Param("user") User itemOwner, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.startDate < :time " +
            "   AND b.endDate > :time ")
    List<Booking> findByItemOwnerCurrent(@Param("user") User itemOwner,
                                         @Param("time") LocalDateTime currentTime, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.endDate < :time ")
    List<Booking> findByItemOwnerPast(@Param("user") User itemOwner,
                                      @Param("time") LocalDateTime currentTime, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.startDate > :time ")
    List<Booking> findByItemOwnerFuture(@Param("user") User itemOwner,
                                        @Param("time") LocalDateTime currentTime, Sort startDate);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.status = :status ")
    List<Booking> findByItemOwnerAndStatus(@Param("user") User itemOwner,
                                           @Param("status") Status status, Sort startDate);
}