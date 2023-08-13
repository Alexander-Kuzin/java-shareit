package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :id ")
    List<Item> findAllByOwnerIdWithBookings(@Param("id") Long userId, Sort sortByIdAsc);

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.id = :id ")
    @NonNull
    Optional<Item> findByIdWithOwner(@Param("id") @NonNull Long id);

    @Query(" SELECT i FROM Item i " +
            "WHERE lower(i.name) LIKE lower(concat('%', :text, '%')) " +
            "   OR lower(i.description) LIKE lower(concat('%', :text, '%')) " +
            "   AND i.available = true ")
    List<Item> search(@Param("text") String text, Sort sortByIdAsc);
}