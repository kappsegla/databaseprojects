package org.example.composite;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BookingId implements Serializable {
    private Long showId;
    private String seatNumber;

    public BookingId() {}
    public BookingId(Long showId, String seatNumber) {
        this.showId = showId;
        this.seatNumber = seatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingId)) return false;
        BookingId that = (BookingId) o;
        return Objects.equals(showId, that.showId) &&
               Objects.equals(seatNumber, that.seatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(showId, seatNumber);
    }
}
