package mate.academy.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {
    @Query("SELECT p FROM Payment p JOIN Rental r ON p.rentalId = "
            + "r.id WHERE r.userId = :userId")
    List<Payment> findPaymentsByUserId(Long userId);

    Optional<Payment> findBySessionId(String sessionId);
}
