package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class AddOrUpdateUserDtoTest {
    @Autowired
    private JacksonTester<AddOrUpdateUserDto> json;

    @Test
    @SneakyThrows
    void serializeTest() {
        AddOrUpdateUserDto dto = AddOrUpdateUserDto.builder()
                .name("userName")
                .email("mail@ya.ru")
                .build();
        JsonContent<AddOrUpdateUserDto> result = json.write(dto);
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.email");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("userName");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mail@ya.ru");
    }

    @Test
    @SneakyThrows
    void deserializeTest() {
        String content = "{\"name\":\"userName\",\"email\":\"mail@ya.ru\"}";
        ObjectContent<AddOrUpdateUserDto> result = json.parse(content);
        assertThat(result).isEqualTo(AddOrUpdateUserDto.builder()
                .name("userName")
                .email("mail@ya.ru")
                .build());
    }
}