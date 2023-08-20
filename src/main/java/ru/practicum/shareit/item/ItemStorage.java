package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.request " +
            "LEFT JOIN FETCH i.bookings " +
            "LEFT JOIN FETCH i.comments " +
            "WHERE i.id = :id ")
    Optional<Item> findById(@Param("id") Long id);

    @Query(value = "SELECT i FROM Item i " +
            "JOIN FETCH i.owner o " +
            "LEFT JOIN FETCH i.request " +
            "LEFT JOIN FETCH i.bookings " +
            "LEFT JOIN FETCH i.comments " +
            "WHERE o.id = :id ",
            countQuery = "SELECT count(i) from Item i " +
                    "WHERE i.owner.id = :id")
    Page<Item> findAllByOwnerId(@Param("id") Long userId, Pageable pageable);

    @Query(value = "SELECT i from Item i " +
            "JOIN FETCH i.owner o " +
            "LEFT JOIN FETCH i.request r " +
            "LEFT JOIN FETCH i.bookings b " +
            "LEFT JOIN FETCH i.comments c " +
            "WHERE (lower(i.name) LIKE lower(concat('%', :text, '%')) " +
            "   OR lower(i.description) LIKE lower(concat('%', :text, '%'))) " +
            "   AND i.available = true ",
            countQuery = "SELECT count(i) from Item i " +
                    "WHERE (lower(i.name) LIKE lower(concat('%', :text, '%')) " +
                    "   OR lower(i.description) LIKE lower(concat('%', :text, '%'))) " +
                    "   AND i.available = true ")
    Page<Item> search(@Param("text") String text, Pageable pageable);
}