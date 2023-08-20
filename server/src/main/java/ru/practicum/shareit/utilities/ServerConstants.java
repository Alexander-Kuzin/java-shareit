package ru.practicum.shareit.utilities;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetCommentDto;

import java.util.Comparator;

@UtilityClass
public class ServerConstants {

    public static final Comparator<Booking> orderByStartDateDesc = (a, b) -> {
        if (a.getStartDate().isAfter(b.getStartDate())) {
            return -1;
        } else if (a.getStartDate().isBefore(b.getStartDate())) {
            return 1;
        }
        return 0;
    };

    public static final Comparator<Booking> orderByStartDateAsc = (a, b) -> {
        if (a.getStartDate().isAfter(b.getStartDate())) {
            return 1;
        } else if (a.getStartDate().isBefore(b.getStartDate())) {
            return -1;
        }
        return 0;
    };

    public static final Comparator<GetCommentDto> orderByCreatedDesc = (a, b) -> {
        if (a.getCreated().isAfter(b.getCreated())) {
            return 1;
        } else if (a.getCreated().isBefore(b.getCreated())) {
            return -1;
        }
        return 0;
    };
}