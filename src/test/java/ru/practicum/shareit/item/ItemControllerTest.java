package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static AddOrUpdateItemDto correctItem;
    private static AddOrUpdateItemDto itemWithoutName;
    private static AddOrUpdateItemDto itemWithoutDescription;
    private static AddOrUpdateItemDto itemWithoutAvailable;
    private static AddCommentDto correctComment;
    private static AddCommentDto commentWithBlankText;
    private static GetItemDto getItemDto;
    private static GetCommentDto getCommentDto;
    private static List<GetItemDto> listOfItems;

    @BeforeAll
    static void beforeAll() {
        correctItem = AddOrUpdateItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();

        itemWithoutName = AddOrUpdateItemDto.builder()
                .description("description")
                .available(true)
                .build();

        itemWithoutDescription = AddOrUpdateItemDto.builder()
                .name("item")
                .available(true)
                .build();

        itemWithoutAvailable = AddOrUpdateItemDto.builder()
                .name("item")
                .description("description")
                .build();

        correctComment = AddCommentDto.builder()
                .text("comment")
                .build();

        commentWithBlankText = AddCommentDto.builder()
                .text(" ")
                .build();

        getItemDto = GetItemDto.builder()
                .id(1L)
                .name(correctItem.getName())
                .description(correctItem.getDescription())
                .available(correctItem.getAvailable())
                .build();

        getCommentDto = GetCommentDto.builder()
                .id(1L)
                .text(correctItem.getDescription())
                .build();

        listOfItems = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfItems.add(getItemDto.toBuilder().id(i + 1L).build());
        }
    }

    @Test
    void getExceptionWithCreateWithoutHeader() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void getExceptionWithCreateItemWithoutName() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemWithoutName);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void getExceptionWithCreateItemWithoutDescription() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemWithoutDescription);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void getExceptionWithCreateItemWithoutAvailable() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemWithoutAvailable);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void createItem() throws Exception {
        when(itemService.create(anyLong(), any(AddOrUpdateItemDto.class)))
                .thenReturn(getItemDto);

        String jsonItem = objectMapper.writeValueAsString(correctItem);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemDto.getId()))
                .andExpect(jsonPath("$.name").value(getItemDto.getName()))
                .andExpect(jsonPath("$.description").value(getItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(getItemDto.getAvailable()));
        verify(itemService, times(1)).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    @SneakyThrows
    void getExceptionWithUpdateWithoutHeader() {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    @SneakyThrows
    void getExceptionWithUpdateItemWithoutName() {
        when(itemService.update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class)))
                .thenReturn(getItemDto);

        String jsonItem = objectMapper.writeValueAsString(itemWithoutName);

        mockMvc.perform(patch("/items/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemDto.getId()))
                .andExpect(jsonPath("$.name").value(getItemDto.getName()))
                .andExpect(jsonPath("$.description").value(getItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(getItemDto.getAvailable()));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    @SneakyThrows
    void getExceptionWithUpdateItemWithoutDescription() {
        when(itemService.update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class)))
                .thenReturn(getItemDto);

        String jsonItem = objectMapper.writeValueAsString(itemWithoutDescription);

        mockMvc.perform(patch("/items/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemDto.getId()))
                .andExpect(jsonPath("$.name").value(getItemDto.getName()))
                .andExpect(jsonPath("$.description").value(getItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(getItemDto.getAvailable()));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class));
    }

    @SneakyThrows
    @Test
    void getExceptionWithUpdateItemWithoutAvailable() {
        when(itemService.update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class)))
                .thenReturn(getItemDto);

        String jsonItem = objectMapper.writeValueAsString(itemWithoutAvailable);

        mockMvc.perform(patch("/items/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemDto.getId()))
                .andExpect(jsonPath("$.name").value(getItemDto.getName()))
                .andExpect(jsonPath("$.description").value(getItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(getItemDto.getAvailable()));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateItemTest() {
        when(itemService.update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class)))
                .thenReturn(getItemDto);

        String jsonItem = objectMapper.writeValueAsString(correctItem);

        mockMvc.perform(patch("/items/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemDto.getId()))
                .andExpect(jsonPath("$.name").value(getItemDto.getName()))
                .andExpect(jsonPath("$.description").value(getItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(getItemDto.getAvailable()));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class));
    }


    @Test
    @SneakyThrows
    void getExceptionWithDeleteWithoutHeader() {
        mockMvc.perform(delete("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).delete(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void deleteItemTest() {
        doNothing().when(itemService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/items/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).delete(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetAllByUserIdWithoutHeader() {
        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetAllByUserIdWithFromLessThen0() {
        mockMvc.perform(get("/items?from=-1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithGetAllByUserIdWithFromMoreThenMaxInt() throws Exception {
        mockMvc.perform(get("/items?from=2147483648")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithGetAllByUserIdWithSizeLessThen1() throws Exception {
        mockMvc.perform(get("/items?size=0")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithGetAllByUserIdWithSizeMoreThen20() throws Exception {
        mockMvc.perform(get("/items?size=21")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getAllByUserId() throws Exception {
        when(itemService.getAllByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfItems);

        mockMvc.perform(get("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(itemService, times(1)).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithGetByItemIdWithoutHeader() throws Exception {
        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getOneById(anyLong(), anyLong());
    }

    @Test
    void getByItemId() throws Exception {
        when(itemService.getOneById(anyLong(), anyLong()))
                .thenReturn(getItemDto);

        mockMvc.perform(get("/items/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemDto.getId()))
                .andExpect(jsonPath("$.name").value(getItemDto.getName()))
                .andExpect(jsonPath("$.description").value(getItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(getItemDto.getAvailable()));
        verify(itemService, times(1)).getOneById(anyLong(), anyLong());
    }

    @Test
    void getExceptionWithSearchWithoutHeader() throws Exception {
        mockMvc.perform(get("/items/search?text=kek")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithSearchWithoutText() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithSearchWithFromLessThen0() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&from=-1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithSearchWithFromMoreThenMaxInt() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&from=2147483648")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithSearchWithSizeLessThen1() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&size=0")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithSearchWithSizeMoreThen21() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&size=21")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void searchTest() throws Exception {
        when(itemService.search(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listOfItems);

        mockMvc.perform(get("/items/search?text=kek")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(itemService, times(1)).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getExceptionWithCommentWithoutHeader() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createComment(anyLong(), anyLong(), any(AddCommentDto.class));
    }

    @Test
    void getExceptionWithCommentWithBlankText() throws Exception {
        String jsonComment = objectMapper.writeValueAsString(commentWithBlankText);

        mockMvc.perform(post("/items/1/comment")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonComment)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createComment(anyLong(), anyLong(), any(AddCommentDto.class));
    }

    @Test
    void shouldCreateComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(AddCommentDto.class)))
                .thenReturn(getCommentDto);

        String jsonComment = objectMapper.writeValueAsString(correctComment);

        mockMvc.perform(post("/items/1/comment")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonComment)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).createComment(anyLong(), anyLong(), any(AddCommentDto.class));
    }
}
