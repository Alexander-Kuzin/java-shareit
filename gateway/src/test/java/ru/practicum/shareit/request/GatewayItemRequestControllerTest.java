package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;


@WebMvcTest(value = GatewayItemRequestController.class)
class GatewayItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GatewayItemRequestController gatewayItemRequestController;
    @MockBean
    private ItemRequestServiceImpl requestService;
    private static AddItemRequestDto correctRequest;
    private static AddItemRequestDto requestWithBlankDescription;
    private static AddItemRequestDto requestWithDescriptionSize1001;
    private static GetItemRequestDto getItemRequestDto;
    private static List<GetItemRequestDto> listOfRequests;

    @BeforeAll
    static void beforeAll() {
        correctRequest = AddItemRequestDto.builder()
                .description("description")
                .build();
        requestWithBlankDescription = AddItemRequestDto.builder()
                .description(" ")
                .build();
        requestWithDescriptionSize1001 = AddItemRequestDto.builder()
                .description("F".repeat(1001))
                .build();
        getItemRequestDto = GetItemRequestDto.builder()
                .id(1L)
                .description("description")
                .build();
        listOfRequests = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfRequests.add(getItemRequestDto.toBuilder().id(i + 1L).build());
        }
    }

    @SpringBootApplication
    static class TestConfiguration {
    }


    @Test
    @SneakyThrows
    void exceptionWithGetAllRequestsWithSizeMoreThen20Test() {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfRequests);

        mockMvc.perform(get("/requests/all?size=21")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(requestService, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void exceptionWithCreateRequestWithRequestWithBlankDescriptionTest() {
        when(requestService.addRequest(anyLong(), any(AddItemRequestDto.class)))
                .thenReturn(getItemRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(requestWithBlankDescription);

        mockMvc.perform(post("/requests")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).addRequest(anyLong(), any(AddItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void exceptionWithCreateRequestWithRequestWithDescriptionSize1001Test() {
        when(requestService.addRequest(anyLong(), any(AddItemRequestDto.class)))
                .thenReturn(getItemRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(requestWithDescriptionSize1001);

        mockMvc.perform(post("/requests")
                        .header(REQUEST_HEADER_USER_ID, "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).addRequest(anyLong(), any(AddItemRequestDto.class));
    }


}