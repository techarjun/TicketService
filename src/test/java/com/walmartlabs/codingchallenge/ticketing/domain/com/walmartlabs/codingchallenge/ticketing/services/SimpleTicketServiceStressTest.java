package com.walmartlabs.codingchallenge.ticketing.domain.com.walmartlabs.codingchallenge.ticketing.services;

import com.walmartlabs.codingchallenge.ticketing.domain.SeatHold;
import com.walmartlabs.codingchallenge.ticketing.domain.Venue;
import com.walmartlabs.codingchallenge.ticketing.services.SimpleTicketService;
import com.walmartlabs.codingchallenge.ticketing.services.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleTicketServiceStressTest {

    private static final Logger LOG = Logger.getLogger(SimpleTicketServiceStressTest.class.getName());
    private static final String EMAIL = "sampleemail";
    private static final int maxBlockSize = 15;
    private static final int threads = 10;
    private static final int seats = 100;
    private static final int rows = 50;
    private static final Venue venue = new Venue(1, rows, seats);


    private CompletionService<SeatHold> holdService;
    private CompletionService<String> reservationService;
    private TicketService service;


    @BeforeEach
    void beforeEach() {
        service = new SimpleTicketService(venue, 60);

        this.holdService = new ExecutorCompletionService<>(Executors.newFixedThreadPool(threads));
        this.reservationService = new ExecutorCompletionService<>(Executors.newFixedThreadPool(threads));
    }

    @Test
    void holdAndReserveSeats() {
        // There should always be seats to start out.
        assertTrue(service.numSeatsAvailable() > 0);

        // The futures containing the SeatHold result from findAndHoldSeats.
        final Set<Future<SeatHold>> holdFutures = ConcurrentHashMap.newKeySet();
        // The ticket holds that have been created.
        List<SeatHold> holds = Collections.synchronizedList(new ArrayList<>());
        //
        Set<Integer> holdIds = ConcurrentHashMap.newKeySet();
        // A set of seats that are currently on hold.
        Set<Integer> heldSeats = ConcurrentHashMap.newKeySet();
        // The start time.
        long start = System.currentTimeMillis();


        // Attempt to hold all of the seats in the venue.
        while (service.numSeatsAvailable() > maxBlockSize) {
            // Concurrently hold tickets.
            Future<SeatHold> f = holdService.submit(() -> {
                final int numTicketsToHold = ThreadLocalRandom.current().nextInt(1, maxBlockSize + 1);
                //LOG.info(String.format("Attempting to hold  %d seats (%d)", numTicketsToHold, Thread.currentThread().getId()));
                SeatHold sh = service.findAndHoldSeats(numTicketsToHold, EMAIL);
                LOG.info(String.format("Hold Success for %d seats id=(%d)", sh.getNumSeats(), sh.getId()));
                return sh;
            });
            holdFutures.add(f);
        }
        long holdTime = (System.currentTimeMillis() - start);
        LOG.info("\nHolding process is complete...Took " + holdTime + " ms \n");


        while (holdFutures.size() > 0) {
            try {
                Future<SeatHold> f = holdService.take();
                holdFutures.remove(f);
                SeatHold h = f.get();

                if ((h != null) && h.getId() != null) {
                    // Save the seat hold for later use.
                    assertFalse(holdIds.contains(h.getId()));
                    holdIds.add(h.getId());
                    holds.add(h);

                    // Add each of the seats held to a set for validation.
                    for (Integer s : h.getSeats()) {

                        // Keep track of the held seats.
                        heldSeats.add(s);
                    }
                }
            } catch (Exception e) {

            }
        }


        final int totalHolds = holds.size();

        // The futures containing the reservation confirmation code.
        final Set<Future<String>> reserveFutures = ConcurrentHashMap.newKeySet();
        // The ticket reservations that have been created.
        final Set<String> reservations = ConcurrentHashMap.newKeySet();

        while (holds.size() > 0) {
            final SeatHold holdToReserve = holds.remove(0);
            Future<String> f = reservationService.submit(() -> {
                return service.reserveSeats(holdToReserve.getId(), holdToReserve.getCustomerEmail());
            });
            reserveFutures.add(f);
        }

        while (reserveFutures.size() > 0) {
            String reservationID = null;
            try {
                Future<String> rf = reservationService.take();
                reserveFutures.remove(rf);
                reservationID = rf.get();
                if (reservationID != null) {
                    assertFalse(reservations.contains(reservationID));
                    reservations.add(reservationID);
                }
            } catch (Exception e) {
                if (reservationID != null) {
                    LOG.warning(String.format("Reservation already processed: %b", reservations.contains(reservationID)));
                }

            }
        }

        LOG.info("\n\n---------- Stress Test Results ----------\n" +
                String.format("\tTotal Hold Requests: %d\n", totalHolds) +
                String.format("\tTotal Seats Held: %d\n", heldSeats.size()) +
                String.format("\tReservations Made: %d", reservations.size()) +
                String.format("\tTime: %d ms\n", (System.currentTimeMillis() - start)));
    }

}

