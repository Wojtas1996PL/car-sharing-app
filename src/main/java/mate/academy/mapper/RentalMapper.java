package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.rental.RentalRequestDto;
import mate.academy.dto.rental.RentalResponseDto;
import mate.academy.model.Rental;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    RentalResponseDto toResponseDto(RentalRequestDto rentalRequestDto);

    RentalResponseDto toResponseDto(Rental rental);

    Rental toModel(RentalResponseDto rentalResponseDto);
}
