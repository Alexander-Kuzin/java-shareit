package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.markers.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class AddOrUpdateUserDto {
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Email не корректный",
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Size(max = 512, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;
}