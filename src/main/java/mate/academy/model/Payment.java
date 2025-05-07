package mate.academy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentType type;
    @Column(name = "session_url", nullable = false)
    private String sessionUrl;
    @Column(name = "session_id", nullable = false)
    private String sessionId;
}
