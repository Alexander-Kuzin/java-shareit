package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestStorageTest {
    private final ItemRequestService requestService;
    private final UserStorage userStorage;
    private final EntityManager entityManager;

    private static User user;
    private static User user2;
    private static AddItemRequestDto requestDto;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .id(1L)
                .name("userName")
                .email("mail@ya.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("userName")
                .email("mail2@ya.ru")
                .build();

        requestDto = AddItemRequestDto.builder()
                .description("need item")
                .build();
    }

    @Test
    void createRequestTest() {
        userStorage.save(user);
        requestService.addRequest(user.getId(), requestDto);

        TypedQuery<ItemRequest> query = entityManager.createQuery("select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", 1L).getSingleResult();

        assertThat(requestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(user, equalTo(request.getRequester()));
        assertNull(request.getItems());
    }

    @Test
    void getRequestByIdTest() {
        userStorage.save(user);
        requestService.addRequest(user.getId(), requestDto);

        GetItemRequestDto request = requestService.getRequestById(user.getId(), 1L);

        assertThat(requestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(List.of(), equalTo(request.getItems()));
    }

    @Test
    void getAllRequestsTest() {
        userStorage.save(user);
        userStorage.save(user2);
        AddItemRequestDto requestDto2 = AddItemRequestDto.builder().description("request2").build();
        AddItemRequestDto requestDto3 = AddItemRequestDto.builder().description("request3").build();
        AddItemRequestDto requestDto4 = AddItemRequestDto.builder().description("request4").build();
        AddItemRequestDto requestDto5 = AddItemRequestDto.builder().description("request5").build();
        AddItemRequestDto requestDto6 = AddItemRequestDto.builder().description("request6").build();
        AddItemRequestDto requestDto7 = AddItemRequestDto.builder().description("request7").build();

        requestService.addRequest(user.getId(), requestDto);
        requestService.addRequest(user2.getId(), requestDto5);
        requestService.addRequest(user2.getId(), requestDto6);
        requestService.addRequest(user2.getId(), requestDto7);
        requestService.addRequest(user.getId(), requestDto2);
        requestService.addRequest(user.getId(), requestDto3);

        requestService.addRequest(user.getId(), requestDto4);

        List<GetItemRequestDto> requests = requestService.getAllRequests(2L, 1, 5);
        log.info(requests.toString());

        Assertions.assertThat(requests)
                .isNotEmpty()
                .hasSize(3)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 6L)
                            .hasFieldOrPropertyWithValue("description", requestDto3.getDescription());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 5L)
                            .hasFieldOrPropertyWithValue("description", requestDto2.getDescription());
                    Assertions.assertThat(list.get(2)).hasFieldOrPropertyWithValue("id", 1L)
                            .hasFieldOrPropertyWithValue("description", requestDto.getDescription());
                });
    }

    @Test
    void getAllRequestsByUserIdTest() {
        userStorage.save(user.toBuilder().build());
        userStorage.save(user2.toBuilder().build());
        AddItemRequestDto requestDto1 = AddItemRequestDto.builder().description("request1").build();
        AddItemRequestDto requestDto2 = AddItemRequestDto.builder().description("request2").build();
        AddItemRequestDto requestDto3 = AddItemRequestDto.builder().description("request3").build();
        AddItemRequestDto requestDto4 = AddItemRequestDto.builder().description("request4").build();
        AddItemRequestDto requestDto5 = AddItemRequestDto.builder().description("request5").build();
        AddItemRequestDto requestDto6 = AddItemRequestDto.builder().description("request6").build();
        AddItemRequestDto requestDto7 = AddItemRequestDto.builder().description("request7").build();

        requestService.addRequest(user.getId(), requestDto1);
        requestService.addRequest(user2.getId(), requestDto5);
        requestService.addRequest(user2.getId(), requestDto6);
        requestService.addRequest(user2.getId(), requestDto7);
        requestService.addRequest(user.getId(), requestDto2);
        requestService.addRequest(user.getId(), requestDto3);
        requestService.addRequest(user.getId(), requestDto4);

        List<GetItemRequestDto> requests1 = requestService.getAllRequestsByUserId(2L);
        log.info(requests1.toString());

        Assertions.assertThat(requests1)
                .isNotEmpty()
                .hasSize(3)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 4L)
                            .hasFieldOrPropertyWithValue("description", requestDto7.getDescription());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 3L)
                            .hasFieldOrPropertyWithValue("description", requestDto6.getDescription());
                    Assertions.assertThat(list.get(2)).hasFieldOrPropertyWithValue("id", 2L)
                            .hasFieldOrPropertyWithValue("description", requestDto5.getDescription());
                });
    }
}