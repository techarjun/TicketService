package com.walmartlabs.codingchallenge.ticketing.domain.com.walmartlabs.codingchallenge.ticketing.services;

import com.walmartlabs.codingchallenge.ticketing.domain.ExceptionHelper;
import com.walmartlabs.codingchallenge.ticketing.domain.SeatHold;
import com.walmartlabs.codingchallenge.ticketing.domain.Venue;
import com.walmartlabs.codingchallenge.ticketing.services.SimpleTicketService;
import com.walmartlabs.codingchallenge.ticketing.services.TicketService;
import com.walmartlabs.codingchallenge.ticketing.services.exceptions.TicketServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTicketServiceTest {

    private static final int VENUE_ID = 1;
    private static final String CUSTOMER_EMAIL = "arjun@tech.com";
    private static final int NUM_ROWS = 3;
    private static final int SEATS_PER_ROW = 3;

    private Venue venue;
    private TicketService ticketService;

    @BeforeAll
    static void setupAll() {

    }

    @BeforeEach
    void setup() {

        venue = new Venue(VENUE_ID, NUM_ROWS, SEATS_PER_ROW);
        ticketService = new SimpleTicketService(venue, 1);
    }

    @Test
    void findAndHoldSeats() {
        int numSeatsToHold = 2;
        SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToHold, CUSTOMER_EMAIL);
        assertEquals(numSeatsToHold, seatHold.getNumSeats());
    }

    @Test
    void hold_noSeatsAvailable() {
        SeatHold hold = ticketService.findAndHoldSeats(2, CUSTOMER_EMAIL);
        assertEquals(NUM_ROWS*SEATS_PER_ROW-2, ticketService.numSeatsAvailable());
    }

    @Test
    void allSeatsAvailable() {
        assertEquals(venue.getTotalSeats(), ticketService.numSeatsAvailable(),
                "Should have all seats " + "available"
        );
    }

    @Test
    void tryToHoldMoreSeatsAfterNoneAvailable() {

        ExceptionHelper.testException(IllegalArgumentException.class,
                () ->  ticketService.findAndHoldSeats(100, CUSTOMER_EMAIL),
                "You are requesting to hold more than the whole venue. "
        );
    }

    @Test
    void seatHoldReducesAvailableSeatCount() {
        int numSeatsToHold = 2;
        SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToHold, CUSTOMER_EMAIL);
        assertEquals(venue.getTotalSeats() - numSeatsToHold, ticketService.numSeatsAvailable(),
                "Seat holds should reduce number of available seats"
        );
    }

    @Test
    void ensureSeatHoldsExpire() throws InterruptedException {
        int numSeatsToHold = 2;
        SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToHold, CUSTOMER_EMAIL);
        Thread.sleep(1200);

        assertEquals(venue.getTotalSeats(), ticketService.numSeatsAvailable(),
                "Venue Seats should match available seats"
        );
    }

    @Test
    void reserveSeats() {
        SeatHold seatHold = ticketService.findAndHoldSeats(2, CUSTOMER_EMAIL);
        assertEquals(seatHold.getNumSeats(), 2, "Should have held 2 seats");

        String reservationCode = ticketService.reserveSeats(seatHold.getId(), CUSTOMER_EMAIL);
        assertEquals("R" + seatHold.getId(), reservationCode,
                "Reservation code should match expected pattern"
        );
    }

    @Test
    void reserveSeats_invalid() {

        try {
            ticketService.reserveSeats(12345, CUSTOMER_EMAIL);
            Assertions.fail("NoSuchSeatHoldException should have been thrown.");
        } catch (TicketServiceException e) {
            assertNotNull(e);
        }
    }

}
