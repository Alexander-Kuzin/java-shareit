package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GatewayUserController.class)
class GatewayUserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private GatewayUserController gatewayUserController;
    private static AddOrUpdateUserDto correctUser;
    private static AddOrUpdateUserDto userWithoutName;
    private static AddOrUpdateUserDto userWithBlankName;
    private static AddOrUpdateUserDto userWithoutEmail;
    private static AddOrUpdateUserDto userWithWrongEmailNoName;
    private static AddOrUpdateUserDto userWithWrongEmailNoDomainSecondLevel;
    private static AddOrUpdateUserDto userWithWrongEmailNoDomainFirstLevel;
    private static AddOrUpdateUserDto userWithWrongEmailNoAt;
    private static AddOrUpdateUserDto userWithWrongEmailToShortDomainFirstLevel;
    private static AddOrUpdateUserDto userWithWrongEmailToLongDomainFirstLevel;
    private static List<GetUserDto> listOfUsers;
    private static GetUserDto getUserDto;

    @BeforeAll
    static void beforeAll() {
        correctUser = AddOrUpdateUserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();

        userWithoutName = AddOrUpdateUserDto.builder()
                .email("user@user.com")
                .build();

        userWithBlankName = AddOrUpdateUserDto.builder()
                .name("")
                .email("user@user.com")
                .build();

        userWithoutEmail = AddOrUpdateUserDto.builder()
                .name("user")
                .build();

        userWithWrongEmailNoName = AddOrUpdateUserDto.builder()
                .name("user")
                .email("@user.com")
                .build();

        userWithWrongEmailNoDomainSecondLevel = AddOrUpdateUserDto.builder()
                .name("user")
                .email("user@.com")
                .build();

        userWithWrongEmailNoDomainFirstLevel = AddOrUpdateUserDto.builder()
                .name("user")
                .email("user@user.")
                .build();

        userWithWrongEmailNoAt = AddOrUpdateUserDto.builder()
                .name("user")
                .email("user.user.com")
                .build();

        userWithWrongEmailToShortDomainFirstLevel = AddOrUpdateUserDto.builder()
                .name("user")
                .email("user@user.c")
                .build();

        userWithWrongEmailToLongDomainFirstLevel = AddOrUpdateUserDto.builder()
                .name("user")
                .email("user@user.commm")
                .build();

        getUserDto = GetUserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        listOfUsers = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            listOfUsers.add(getUserDto.toBuilder().id(i + 2L).build());
        }
    }

    @SpringBootApplication
    static class TestConfiguration {
    }


    @Test
    void getExceptionWithCreateUserWithoutName() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithoutName);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithCreateUserWithBlankName() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithBlankName);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithCreateUserWithoutEmail() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithoutEmail);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithCreateUserWithWrongEmailNoName() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoName);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithCreateUserWithWrongEmailNoDomainSecondLevel() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoDomainSecondLevel);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithCreateUserWithWrongEmailNoDomainFirstLevel() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoDomainFirstLevel);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithCreateUserWithWrongEmailNoAt() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoAt);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    @SneakyThrows
    void getExceptionWithCreateUserWithWrongEmailToShortDomainFirstLevel() {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailToShortDomainFirstLevel);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    @SneakyThrows
    void getExceptionWithCreateUserWithWrongEmailToLongDomainFirstLevel() {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailToLongDomainFirstLevel);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

}