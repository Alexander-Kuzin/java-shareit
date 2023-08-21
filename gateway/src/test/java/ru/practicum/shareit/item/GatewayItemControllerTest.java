package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@WebMvcTest(value = GatewayItemController.class)
class GatewayItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemServiceImpl itemService;
    @MockBean
    private GatewayItemController gatewayItemController;
    private static AddOrUpdateItemDto correctItem;
    private static AddOrUpdateItemDto itemWithoutName;
    private static AddOrUpdateItemDto itemWithoutDescription;
    private static AddOrUpdateItemDto itemWithoutAvailable;
    private static GetItemDto getItemDto;
    private static GetCommentDto getCommentDto;
    private static List<GetItemDto> listOfItems;

    @SpringBootApplication
    static class TestConfiguration {
    }

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

}