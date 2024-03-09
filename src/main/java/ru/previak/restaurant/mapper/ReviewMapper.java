package ru.previak.restaurant.mapper;

import org.mapstruct.Mapper;
import ru.previak.restaurant.dto.ReviewDTO;
import ru.previak.restaurant.entities.ReviewEntity;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDTO reviewEntity2ReviewDTO(ReviewEntity reviewEntity);
    ReviewEntity reviewDTO2ReviewEntity(ReviewDTO reviewDTO);
}
