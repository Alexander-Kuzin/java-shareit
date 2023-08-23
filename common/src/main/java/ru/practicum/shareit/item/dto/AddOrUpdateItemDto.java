package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.markers.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class AddOrUpdateItemDto {
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 512, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}