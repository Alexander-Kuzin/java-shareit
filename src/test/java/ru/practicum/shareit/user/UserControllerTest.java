package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

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
    void getExceptionWithCreateUserWithWrongEmailToLongDomainFirstLevel()  {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailToLongDomainFirstLevel);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any(AddOrUpdateUserDto.class));
    }

    @Test
    @SneakyThrows
    void createUserTest() {
        when(userService.create(any(AddOrUpdateUserDto.class)))
                .thenReturn(getUserDto);

        String jsonUser = objectMapper.writeValueAsString(correctUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));

        verify(userService, times(1)).create(correctUser);
    }

    @Test
    @SneakyThrows
    void getExceptionWithUpdateUserWithWrongEmailNoName()  {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoName);

        mockMvc.perform(patch("/users/1")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithUpdateUserWithWrongEmailNoDomainSecondLevel() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoDomainSecondLevel);

        mockMvc.perform(patch("/users/1")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithUpdateUserWithWrongEmailNoDomainFirstLevel() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoDomainFirstLevel);

        mockMvc.perform(patch("/users/1")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithUpdateUserWithWrongEmailNoAt() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailNoAt);

        mockMvc.perform(patch("/users/1")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithUpdateUserWithWrongEmailToShortDomainFirstLevel() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailToShortDomainFirstLevel);

        mockMvc.perform(patch("/users/1")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void getExceptionWithUpdateUserWithWrongEmailToLongDomainFirstLevel() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userWithWrongEmailToLongDomainFirstLevel);

        mockMvc.perform(patch("/users/1")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void updateUserWithoutName() throws Exception {
        when(userService.update(anyLong(), any(AddOrUpdateUserDto.class)))
                .thenReturn(getUserDto);

        String jsonUser = objectMapper.writeValueAsString(userWithoutName);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));
        verify(userService, times(1)).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void updateUserWithoutEmail() throws Exception {
        when(userService.update(anyLong(), any(AddOrUpdateUserDto.class)))
                .thenReturn(getUserDto);

        String jsonUser = objectMapper.writeValueAsString(userWithoutEmail);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));
        verify(userService, times(1)).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(anyLong(), any(AddOrUpdateUserDto.class)))
                .thenReturn(getUserDto);

        String jsonUser = objectMapper.writeValueAsString(correctUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));
        verify(userService, times(1)).update(anyLong(), any(AddOrUpdateUserDto.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteById(anyLong());

        mockMvc.perform(delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON));
        verify(userService, times(1)).deleteById(anyLong());
    }

    @Test
    void getById() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(getUserDto);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));
        verify(userService, times(1)).getById(anyLong());
    }

    @Test
    void getExceptionWithGetAllWithFromLessThen0() throws Exception {
        mockMvc.perform(get("/users?from=-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).getById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetAllWithFromMoreThenMaxInt() throws Exception {
        mockMvc.perform(get("/users?from=2147483648")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).getById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetAllWithFromSizeLessThen1() throws Exception {
        mockMvc.perform(get("/users?size=0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).getById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetAllWithSizeMoreThen20() throws Exception {
        mockMvc.perform(get("/users?size=21")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).getById(anyLong());
    }

    @Test
    void shouldGetAll() throws Exception {
        when(userService.getAll(anyInt(), anyInt()))
                .thenReturn(listOfUsers);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(userService, times(1)).getAll(anyInt(), anyInt());
    }
}