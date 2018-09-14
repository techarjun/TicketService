package com.walmartlabs.codingchallenge.ticketing.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeatHoldTest {

    private static final  Seat[] SEATS_TO_HOLD = new Seat[]

    {
        new Seat(1, 1, 1, 1),
                new Seat(2, 1, 2, 2),
                new Seat(3, 1, 3, 3)
    };

    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = new Venue(1, 1, 3);
    }

    @Test
    void getNumSeats() {
        SeatHold seatHold = new SeatHold(1, 3);
        assertEquals(3, seatHold.getNumSeats(), "Number of seats held should equal requested seats");
    }

    @Test
    void requestMoreSeatsThanAvailable() {
        SeatHold seatHold = new SeatHold(1, venue.getTotalSeats());
        assertEquals(
                venue.getTotalSeats(),
                seatHold.getNumSeats(),
                "Number of seats held should equal " + "total number of seats in the location"
        );
    }

}
