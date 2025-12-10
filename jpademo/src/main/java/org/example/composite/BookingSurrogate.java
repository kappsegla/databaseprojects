package org.example.composite;

import jakarta.persistence.*;

@Entity
@Table(
    name = "booking_surrogate",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"show_id", "seat_number"})
    }
)
public class BookingSurrogate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;   // Surrogate key

    @Column(name = "show_id", nullable = false)
    private Long showId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "customer_name")
    private String customerName;

    public BookingSurrogate() {}
    public BookingSurrogate(Long showId, String seatNumber, String customerName) {
        this.showId = showId;
        this.seatNumber = seatNumber;
        this.customerName = customerName;
    }
}
