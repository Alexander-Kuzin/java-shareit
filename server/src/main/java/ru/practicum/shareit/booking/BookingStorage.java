package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.id = :id ")
    Optional<Booking> findById(@Param("id") Long id);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item " +
            "WHERE bk = :user ",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.booker = :user ")
    Page<Booking> findAllByBooker(@Param("user") User booker, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item " +
            "WHERE bk = :user " +
            "   AND b.startDate < :time " +
            "   AND b.endDate > :time",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.booker = :user " +
                    "   AND b.startDate < :time " +
                    "   AND b.endDate > :time")
    Page<Booking> findAllByBookerAndCurrent(@Param("user") User booker,
                                            @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item " +
            "WHERE bk = :user " +
            "   AND b.endDate < :time",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.booker = :user " +
                    "   AND b.endDate < :time")
    Page<Booking> findAllByBookerAndPast(@Param("user") User booker,
                                         @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item " +
            "WHERE bk = :user " +
            "   AND b.startDate > :time",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.booker = :user " +
                    "   AND b.startDate > :time")
    Page<Booking> findAllByBookerAndFuture(@Param("user") User booker,
                                           @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item " +
            "WHERE bk = :user " +
            "   AND b.status = :status",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.booker = :user " +
                    "   AND b.status = :status")
    Page<Booking> findAllByBookerAndStatus(@Param("user") User booker,
                                           @Param("status") Status status, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.item.owner = :user ",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.item.owner= :user ")
    List<Booking> findAllByItemOwner(@Param("user") User itemOwner, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.startDate < :time " +
            "   AND b.endDate > :time",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.item.owner = :user " +
                    "   AND b.startDate < :time " +
                    "   AND b.endDate > :time")
    Page<Booking> findAllByItemOwnerAndCurrent(@Param("user") User itemOwner,
                                               @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.endDate < :time",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.item.owner = :user " +
                    "   AND b.endDate < :time")
    Page<Booking> findAllByItemOwnerAndPast(@Param("user") User itemOwner,
                                            @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.startDate > :time",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.item.owner = :user " +
                    "   AND b.startDate > :time")
    Page<Booking> findAllByItemOwnerAndFuture(@Param("user") User itemOwner,
                                              @Param("time") LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker bk " +
            "JOIN FETCH b.item i " +
            "WHERE i.owner = :user " +
            "   AND b.status = :status",
            countQuery = "SELECT b FROM Booking b " +
                    "WHERE b.item.owner = :user " +
                    "   AND b.status = :status")
    Page<Booking> findAllByItemOwnerAndStatus(@Param("user") User itemOwner,
                                              @Param("status") Status status, Pageable pageable);
}