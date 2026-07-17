package ru.practicum.shareit.request.dto;


import lombok.*;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {

    private long id;


    private String description;

    private LocalDateTime created;

    private List<RequestDto> items;

}
