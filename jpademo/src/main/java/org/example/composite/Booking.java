package org.example.composite;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {

    @EmbeddedId
    private BookingId id;

    @Column(name = "customer_name")
    private String customerName;

    public Booking() {}
    public Booking(Long showId, String seatNumber, String customerName) {
        this.id = new BookingId(showId, seatNumber);
        this.customerName = customerName;
    }
}