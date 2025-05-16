package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    @Query(value = "SELECT r FROM Rental r LEFT JOIN FETCH r.cars "
            + "WHERE r.user.id =:userId AND r.isActive = true")
    Optional<Rental> findRentalFromUser(Long userId);
}
