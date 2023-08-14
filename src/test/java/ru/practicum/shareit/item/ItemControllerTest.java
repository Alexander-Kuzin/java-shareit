package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    void GetExceptionWithCreateWithoutHeader() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void GetExceptionWithCreateItemWithoutName() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemWithoutName);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void GetExceptionWithCreateItemWithoutDescription() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemWithoutDescription);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void GetExceptionWithCreateItemWithoutAvailable() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemWithoutAvailable);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).create(anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void CreateItem() throws Exception {
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
    void GetExceptionWithUpdateWithoutHeader() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).update(anyLong(), anyLong(), any(AddOrUpdateItemDto.class));
    }

    @Test
    void GetExceptionWithUpdateItemWithoutName() throws Exception {
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
    void GetExceptionWithUpdateItemWithoutDescription() throws Exception {
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

    @Test
    void GetExceptionWithUpdateItemWithoutAvailable() throws Exception {
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
    void UpdateItem() throws Exception {
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
    void GetExceptionWithDeleteWithoutHeader() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).delete(anyLong(), anyLong());
    }

    @Test
    void DeleteItem() throws Exception {
        doNothing().when(itemService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/items/1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).delete(anyLong(), anyLong());
    }

    @Test
    void GetExceptionWithGetAllByUserIdWithoutHeader() throws Exception {
        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void GetExceptionWithGetAllByUserIdWithFromLessThen0() throws Exception {
        mockMvc.perform(get("/items?from=-1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void GetExceptionWithGetAllByUserIdWithFromMoreThenMaxInt() throws Exception {
        mockMvc.perform(get("/items?from=2147483648")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void GetExceptionWithGetAllByUserIdWithSizeLessThen1() throws Exception {
        mockMvc.perform(get("/items?size=0")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void GetExceptionWithGetAllByUserIdWithSizeMoreThen20() throws Exception {
        mockMvc.perform(get("/items?size=21")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void GetAllByUserId() throws Exception {
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
    void GetExceptionWithGetByItemIdWithoutHeader() throws Exception {
        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getOneById(anyLong(), anyLong());
    }

    @Test
    void GetByItemId() throws Exception {
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
    void GetExceptionWithSearchWithoutHeader() throws Exception {
        mockMvc.perform(get("/items/search?text=kek")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void GetExceptionWithSearchWithoutText() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void GetExceptionWithSearchWithFromLessThen0() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&from=-1")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldGetExceptionWithSearchWithFromMoreThenMaxInt() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&from=2147483648")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldGetExceptionWithSearchWithSizeLessThen1() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&size=0")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldGetExceptionWithSearchWithSizeMoreThen21() throws Exception {
        mockMvc.perform(get("/items/search?text=kek&size=21")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldSearch() throws Exception {
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
    void shouldGetExceptionWithCommentWithoutHeader() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).createComment(anyLong(), anyLong(), any(AddCommentDto.class));
    }

    @Test
    void shouldGetExceptionWithCommentWithBlankText() throws Exception {
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
