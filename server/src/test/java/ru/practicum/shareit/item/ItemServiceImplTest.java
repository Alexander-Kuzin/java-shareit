package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.MethodArgumentException;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.model.Status;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.utilities.ServerConstants.orderByCreatedDesc;


class ItemServiceImplTest {
    private static ItemService itemService;
    private static ItemStorage itemStorage;
    private static UserStorage userStorage;
    private static CommentStorage commentStorage;
    private static ItemRequestStorage requestStorage;

    private static User user;
    private static ItemRequest request;
    private static LocalDateTime currentTime;
    private static AddOrUpdateItemDto createItemDto;
    private static AddOrUpdateItemDto updateItemDto;
    private static AddCommentDto createCommentDto;
    private static Item item;
    private static Item updatedItem;
    private static Booking booking;
    private static Comment comment;
    private static List<Item> listOfItems;

    @BeforeAll
    static void beforeAll() {
        currentTime = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .name("userName")
                .email("email@ya.ru")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .requester(user)
                .created(currentTime)
                .items(new HashSet<>())
                .build();

        createItemDto = AddOrUpdateItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();

        updateItemDto = AddOrUpdateItemDto.builder()
                .name("updatedName")
                .description("updatedDescription")
                .available(false)
                .build();

        createCommentDto = AddCommentDto.builder()
                .text("comment")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .request(request)
                .owner(user)
                .bookings(Set.of())
                .comments(new TreeSet<>())
                .build();

        updatedItem = Item.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .available(false)
                .request(request)
                .request(request)
                .owner(user)
                .bookings(Set.of())
                .comments(new TreeSet<>())
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .author(user)
                .created(LocalDateTime.now())
                .build();

        item.setBookings(Set.of(booking));

        listOfItems = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfItems.add(item.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        itemStorage = Mockito.mock(ItemStorage.class);
        userStorage = Mockito.mock(UserStorage.class);
        commentStorage = Mockito.mock(CommentStorage.class);
        requestStorage = Mockito.mock(ItemRequestStorage.class);
        itemService = new ItemServiceImpl(itemStorage, userStorage, commentStorage, requestStorage);
    }

    @Test
    void createItemWithRequest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.save(any(Item.class)))
                .thenReturn(item);

        GetItemDto itemDto = itemService.create(user.getId(), createItemDto);

        assertThat(itemDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", createItemDto.getName())
                .hasFieldOrPropertyWithValue("description", createItemDto.getDescription())
                .hasFieldOrPropertyWithValue("requestId", 1L)
                .hasFieldOrPropertyWithValue("lastBooking", null)
                .hasFieldOrPropertyWithValue("nextBooking", null)
                .hasFieldOrPropertyWithValue("comments", new TreeSet<>(orderByCreatedDesc));
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void getExceptionWithCreateWithNotFoundUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong())).thenReturn(null);
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.create(1L, createItemDto));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void getExceptionWithCreateWithNotFoundRequest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.save(any(Item.class)))
                .thenReturn(item);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.create(1L, createItemDto));

        assertEquals("Request with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void getExceptionWithUpdateItemWithNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        when(itemStorage.save(updatedItem))
                .thenReturn(updatedItem);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.update(1L, 1L, updateItemDto));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void getExceptionWithUpdateItemWithNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.save(updatedItem))
                .thenReturn(updatedItem);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.update(1L, 1L, updateItemDto));

        assertEquals("Item with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void getExceptionWithUpdateItemWithNotFoundOwner() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        when(itemStorage.save(updatedItem))
                .thenReturn(updatedItem);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.update(1L, 1L, updateItemDto));

        assertEquals("User with ID = 2 has no items with ID = 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void deleteItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        doNothing().when(itemStorage).deleteById(anyLong());

        itemService.delete(user.getId(), item.getId());

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).deleteById(anyLong());
    }

    @Test
    void getExceptionWithDeleteItemWithNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestStorage.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong()))
                .thenReturn(null);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item.toBuilder().build()));
        doNothing().when(itemStorage).deleteById(anyLong());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.delete(1L, 1L));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(itemStorage, never()).deleteById(anyLong());
    }

    @Test
    void getExceptionWithDeleteItemWithNotFoundItem() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong())).thenReturn(null);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());
        doNothing().when(itemStorage).deleteById(anyLong());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.delete(1L, 1L));

        assertEquals("Item with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).deleteById(anyLong());
    }

    @Test
    void getExceptionWithDeleteItemWithNotFoundOwner() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(request));
        when(commentStorage.findById(anyLong())).thenReturn(null);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item.toBuilder().build()));
        doNothing().when(itemStorage).deleteById(anyLong());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.delete(1L, 1L));

        assertEquals("User with ID = 2 has no items with ID = 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).deleteById(anyLong());
    }

    @Test
    void getByIdByOwner() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item.toBuilder().build()));

        itemService.getOneById(user.getId(), item.getId());

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
    }

    @Test
    void testGetByIdByNotOwner() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item.toBuilder().build()));

        itemService.getOneById(2L, item.getId());

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
    }

    @Test
    void testGetExceptionGetByIdByWithNotFoundUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item.toBuilder().build()));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.getOneById(1L, 1L));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
    }

    @Test
    void testGetExceptionGetByIdByWithNotFoundItem() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.getOneById(1L, 1L));

        assertEquals("Item with ID 1 not found",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
    }

    @Test
    void testGetAllByUserIdByOwner() {
        when(itemStorage.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(listOfItems));

        List<GetItemDto> items = itemService.getAllByUserId(1L, 7, 3);

        org.assertj.core.api.Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "itemName");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "itemDescription");
                });
        verify(userStorage, never()).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllByUserIdByNotOwner() {
        when(itemStorage.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(listOfItems));

        List<GetItemDto> items = itemService.getAllByUserId(2L, 7, 3);

        org.assertj.core.api.Assertions.assertThat(items)
                .isEmpty();
        verify(userStorage, never()).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void testSearch() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.search(anyString(), any(Pageable.class))).thenReturn(new PageImpl<>(listOfItems));

        List<GetItemDto> items = itemService.search(1L, "text", 7, 3);

        org.assertj.core.api.Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "itemName");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "itemDescription");
                });
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).search(anyString(), any(Pageable.class));
    }

    @Test
    void testExceptionWithSearchNotFoundUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.search(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(listOfItems));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.search(1L, "text", 7, 3));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).search(anyString(), any(Pageable.class));
    }

    @Test
    void testGetEmptyListWithSearchWithBlankText() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.search(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(listOfItems));

        List<GetItemDto> items = itemService.search(1L, " ", 7, 3);

        org.assertj.core.api.Assertions.assertThat(items)
                .isEmpty();
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).search(anyString(), any(Pageable.class));
    }

    @Test
    void testCreateComment() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        itemService.createComment(1L, 1L, createCommentDto);

        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(commentStorage, times(1)).save(any(Comment.class));
    }

    @Test
    void testGetExceptionWithCreateCommentWithNotFoundUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.createComment(1L, 1L, createCommentDto));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    void testGetExceptionWithCreateCommentWithNotFoundItem() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.createComment(1L, 1L, createCommentDto));

        assertEquals("Item with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    void testGetExceptionWithCreateCommentWithNotFoundBooking() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(comment);

        final MethodArgumentException exception = Assertions.assertThrows(
                MethodArgumentException.class,
                () -> itemService.createComment(2L, 1L, createCommentDto));

        assertEquals("User ID = 2 did not book item ID = 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(requestStorage, never()).findById(anyLong());
        verify(commentStorage, never()).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(commentStorage, never()).save(any(Comment.class));
    }
}
