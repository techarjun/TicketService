package com.walmartlabs.codingchallenge.ticketing.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeatTest {

    private static final String TEST_ID = "TestSeat";
    private Seat seat;

    @BeforeEach
    void setup() {
        this.seat = new Seat( 1, 1, 1, 1);
    }

    @Test
    void getId() {
        assertEquals(1, seat.getSeatId(), "Expect seat ID not to change");
    }

    @Test
    void isAvailable() {
        assertEquals(true, seat.isAvailable(), "Seat should start out as available");
    }


    @Test
    void reserve() {

        seat.setReserved(true);
        assertAll("check conditions",
                () -> assertEquals(false, seat.isAvailable(), "Reserving a seat should also make it unavailable"),
                () -> assertEquals(true, seat.isReserved(), "Reserving a seat should mark it reserved")
        );
    }

    @Test
    void cancelReservation() {

        seat.setReserved(true);
        assertAll("before cancellation",
                () -> assertEquals(false, seat.isAvailable(), "Make sure seat is available"),
                () -> assertEquals(true, seat.isReserved(), "Make sure seat got reserved")
        );
        seat.setReserved(false);
        assertAll("after cancellation",
                () -> assertEquals(true, seat.isAvailable(), "A released seat should be available for reservation again"),
                () -> assertEquals(false, seat.isReserved(), "Seat should not be reserved")
        );
    }

    @Test
    void reserveAlreadyReservedSeat() {
        seat.setReserved(false);
        seat.setReserved(true);
        ExceptionHelper.testException(
                IllegalStateException.class,
                () ->  seat.setReserved(true),
                "Seat is already reserved"
        );
    }


    @Test
    void hold() {
        seat.setHold(true);
        assertAll("check conditions",
                () -> assertEquals(false, seat.isAvailable(), "Holding a seat should make it unavailable"),
                () -> assertEquals(false, seat.isReserved(), "Seat should not be reserved")
        );
    }

    @Test
    void cancelHold() {
        seat.setHold(true);
        assertAll("ensure hold",
                () -> assertEquals(false, seat.isAvailable(), "Make sure seat got held"),
                () -> assertEquals(false, seat.isReserved(), "Seat should not be reserved")
        );
        seat.setHold(false);
        assertAll("check cancellation",
                () -> assertEquals(true, seat.isAvailable(), "Releasing an unreserved seat should work"),
                () -> assertEquals(false, seat.isReserved(), "Seat should not be reserved")
        );

    }





}
