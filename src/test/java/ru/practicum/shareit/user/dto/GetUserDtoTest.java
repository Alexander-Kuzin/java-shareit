package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class GetUserDtoTest {
    @Autowired
    private JacksonTester<GetUserDto> json;

    @SneakyThrows
    @Test
    void serializeTest() {
        GetUserDto dto = GetUserDto.builder()
                .id(1L)
                .name("userName")
                .email("mail@ya.ru")
                .build();

        JsonContent<GetUserDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("userName");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mail@ya.ru");
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        String content = "{\"id\":\"1\",\"name\":\"userName\",\"email\":\"mail@ya.ru\"}";

        ObjectContent<GetUserDto> result = json.parse(content);

        assertThat(result).isEqualTo(GetUserDto.builder()
                .id(1L)
                .name("userName")
                .email("mail@ya.ru")
                .build());
    }

    @Test
    void getUserForGetBookingDtoTest() {
        GetUserForGetBookingDto booker = GetUserForGetBookingDto.builder()
                .id(2L)
                .build();
        booker.setId(4L);
        assertEquals(booker.getId(), booker.getId());
        assertEquals(booker.toString(), booker.toString());
        assertEquals(booker.toString(), booker.toString());
        assertEquals(booker, booker);
    }
}