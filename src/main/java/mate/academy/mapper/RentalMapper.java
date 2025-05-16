package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.rental.RentalDto;
import mate.academy.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cars", target = "cars")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "rentalDate", target = "rentalDate")
    @Mapping(source = "returnDate", target = "returnDate")
    @Mapping(source = "actualReturnDate", target = "actualReturnDate")
    RentalDto toDto(Rental rental);

    Rental toModel(RentalDto rentalDto);
}
