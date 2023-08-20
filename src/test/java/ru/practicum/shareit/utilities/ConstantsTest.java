package ru.practicum.shareit.utilities;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.utilities.Constants.*;

class ConstantsTest {

    @Test
    void checkConstants() {
        assertEquals("X-Sharer-User-Id", REQUEST_HEADER_USER_ID);
        assertEquals(SORT_BY_START_DATE_DESC, Sort.by(Sort.Direction.DESC, "startDate"));
        assertEquals(SORT_BY_ID_ASC, Sort.by(Sort.Direction.ASC, "id"));
        assertEquals(SORT_BY_CREATED_DESC, Sort.by(Sort.Direction.DESC, "created"));
    }
}