package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemStorageTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final EntityManager entityManager;

    private static AddOrUpdateItemDto createUpdateItemDto;
    private static AddOrUpdateUserDto createUpdateUserDto;

    @BeforeAll
    static void beforeAll() {
        createUpdateItemDto = AddOrUpdateItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        createUpdateUserDto = AddOrUpdateUserDto.builder()
                .name("userName")
                .email("user@ya.ru")
                .build();
    }

    @Test
    void CreateItem() {
        userService.create(createUpdateUserDto);
        itemService.create(1L, createUpdateItemDto);

        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", 1L).getSingleResult();

        assertThat(createUpdateItemDto.getName(), equalTo(item.getName()));
        assertThat(createUpdateItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(createUpdateItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertNull(createUpdateItemDto.getRequestId());
    }

    @Test
    void UpdateItem() {
        userService.create(createUpdateUserDto);
        itemService.create(1L, createUpdateItemDto);

        AddOrUpdateItemDto updateItemDto = createUpdateItemDto.toBuilder()
                .name("newName")
                .description("newDescription")
                .available(false)
                .build();

        itemService.update(1L, 1L, updateItemDto);

        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", 1L).getSingleResult();

        assertThat(updateItemDto.getName(), equalTo(item.getName()));
        assertThat(updateItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(updateItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertNull(createUpdateItemDto.getRequestId());
    }

    @Test
    void DeleteItem() {
        userService.create(createUpdateUserDto);
        itemService.create(1L, createUpdateItemDto);

        assertThat(itemService.getAllByUserId(1L, 0, 20).size(), equalTo(1));

        itemService.delete(1L, 1L);

        assertThat(itemService.getAllByUserId(1L, 0, 20).size(), equalTo(0));
    }

    @Test
    void GetOneById() {
        userService.create(createUpdateUserDto);
        itemService.create(1L, createUpdateItemDto);

        GetItemDto item = itemService.getOneById(1L, 1L);

        assertThat(createUpdateItemDto.getName(), equalTo(item.getName()));
        assertThat(createUpdateItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(createUpdateItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertNull(createUpdateItemDto.getRequestId());
    }

    @Test
    void GetAllByUserId() {
        AddOrUpdateItemDto itemDto2 = createUpdateItemDto.toBuilder().name("name2").build();
        AddOrUpdateItemDto itemDto3 = createUpdateItemDto.toBuilder().name("name3").build();
        AddOrUpdateItemDto itemDto4 = createUpdateItemDto.toBuilder().name("name4").build();
        AddOrUpdateItemDto itemDto5 = createUpdateItemDto.toBuilder().name("name5").build();
        AddOrUpdateItemDto itemDto6 = createUpdateItemDto.toBuilder().name("name6").build();
        AddOrUpdateItemDto itemDto7 = createUpdateItemDto.toBuilder().name("name7").build();

        userService.create(createUpdateUserDto);

        itemService.create(1L, createUpdateItemDto);
        itemService.create(1L, itemDto2);
        itemService.create(1L, itemDto3);
        itemService.create(1L, itemDto4);
        itemService.create(1L, itemDto5);
        itemService.create(1L, itemDto6);
        itemService.create(1L, itemDto7);

        List<GetItemDto> items = itemService.getAllByUserId(1L,5, 2);

        Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 6L);
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", itemDto6.getName());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 7L);
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", itemDto7.getName());
                });
    }

    @Test
    void Search() {
        AddOrUpdateItemDto itemDto2 = createUpdateItemDto.toBuilder().name("name2").build();
        AddOrUpdateItemDto itemDto3 = createUpdateItemDto.toBuilder().name("name3").build();
        AddOrUpdateItemDto itemDto4 = createUpdateItemDto.toBuilder().name("name4").build();
        AddOrUpdateItemDto itemDto5 = createUpdateItemDto.toBuilder().name("name5").build();
        AddOrUpdateItemDto itemDto6 = createUpdateItemDto.toBuilder().name("name6 name4").build();
        AddOrUpdateItemDto itemDto7 = createUpdateItemDto.toBuilder().name("name7").description("name4").build();

        userService.create(createUpdateUserDto);

        itemService.create(1L, createUpdateItemDto);
        itemService.create(1L, itemDto2);
        itemService.create(1L, itemDto3);
        itemService.create(1L, itemDto4);
        itemService.create(1L, itemDto5);
        itemService.create(1L, itemDto6);
        itemService.create(1L, itemDto7);

        List<GetItemDto> items = itemService.search(1L, "name4", 0, 2);

        Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 4L);
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", itemDto4.getName());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 6L);
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", itemDto6.getName());
                });
    }
}