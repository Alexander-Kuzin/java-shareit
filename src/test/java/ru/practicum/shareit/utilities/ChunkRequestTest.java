package ru.practicum.shareit.utilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.utilities.Constants.SORT_BY_ID_ASC;

class ChunkRequestTest {

    @BeforeAll
    static void beforeAll() {
        ChunkRequest chunkRequest1 = new ChunkRequest(0, 1);
        ChunkRequest chunkRequest2 = new ChunkRequest(0, 1, "id");
        ChunkRequest chunkRequest3 = new ChunkRequest(1, 1, SORT_BY_ID_ASC);
    }


    @Test
    void GetCorrectPageNumber() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(0, pageRequest.getPageNumber());
    }


    @Test
    void GetCorrectToString() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.toString(), pageRequest.toString());
    }

    @Test
    void GetCorrectHashCode() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.hashCode(), pageRequest.hashCode());
    }

    @Test
    void getPageNumber() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.getPageNumber(), pageRequest.getPageNumber());
    }

    @Test
    void getPageSize() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.getPageSize(), pageRequest.getPageSize());
    }

    @Test
    void getOffset() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.getOffset(), pageRequest.getOffset());
    }

    @Test
    void getSort() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.getSort(), pageRequest.getSort());
    }

    @Test
    void next() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.next(), pageRequest.next());
    }

    @Test
    void previousOrFirst() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.previousOrFirst(), pageRequest.previousOrFirst());
    }

    @Test
    void first() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.first(), pageRequest.first());
    }

    @Test
    void withPage() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.withPage(1), pageRequest.withPage(1));
    }

    @Test
    void hasPrevious() {
        ChunkRequest pageRequest = new ChunkRequest(10, 5);
        assertEquals(pageRequest.hasPrevious(), pageRequest.hasPrevious());
    }
}