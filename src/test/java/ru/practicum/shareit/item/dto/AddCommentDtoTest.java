package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class AddCommentDtoTest {
    @Autowired
    private JacksonTester<AddCommentDto> json;

    @SneakyThrows
    @Test
    void serializeTest() {
        AddCommentDto dto = AddCommentDto.builder()
                .text("text")
                .build();
        JsonContent<AddCommentDto> result = json.write(dto);
        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    @SneakyThrows
    void deserializeTest() {
        String content = "{\"text\":\"commentText\"}";
        ObjectContent<AddCommentDto> result = json.parse(content);
        assertThat(result).isEqualTo(AddCommentDto.builder()
                .text("commentText")
                .build());
    }
}