package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.rental.RentalDto;
import mate.academy.model.Rental;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    RentalDto toDto(Rental rental);

    Rental toModel(RentalDto rentalDto);
}
