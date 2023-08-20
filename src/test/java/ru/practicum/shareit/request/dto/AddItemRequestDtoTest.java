package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class AddItemRequestDtoTest {
    @Autowired
    private JacksonTester<AddItemRequestDto> json;

    @Test
    @SneakyThrows
    void serializeTest() {
        AddItemRequestDto dto = AddItemRequestDto.builder()
                .description("requestText")
                .build();
        JsonContent<AddItemRequestDto> result = json.write(dto);
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("requestText");
    }

    @Test
    @SneakyThrows
    void deserializeTest() {
        String content = "{\"description\":\"requestText\"}";
        ObjectContent<AddItemRequestDto> result = json.parse(content);
        assertThat(result).isEqualTo(AddItemRequestDto.builder()
                .description("requestText")
                .build());
    }
}