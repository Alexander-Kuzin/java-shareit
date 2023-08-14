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
class AddOrUpdateItemDtoTest {
    @Autowired
    private JacksonTester<AddOrUpdateItemDto> json;

    @Test
    @SneakyThrows
    void serializeTest() {
        AddOrUpdateItemDto dto = AddOrUpdateItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<AddOrUpdateItemDto> result = json.write(dto);
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPathNumberValue("$.requestId");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("itemDescription");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    @SneakyThrows
    void deserializeTest() {
        String content = "{\"name\":\"itemName\",\"description\":\"itemDescription\",\"available\":\"true\",\"requestId\":\"1\"}";
        ObjectContent<AddOrUpdateItemDto> result = json.parse(content);

        assertThat(result).isEqualTo(AddOrUpdateItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build());
    }
}