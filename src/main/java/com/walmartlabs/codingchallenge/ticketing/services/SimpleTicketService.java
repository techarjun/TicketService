package com.walmartlabs.codingchallenge.ticketing.services;

import com.walmartlabs.codingchallenge.ticketing.domain.SeatHold;
import com.walmartlabs.codingchallenge.ticketing.domain.Venue;
import com.walmartlabs.codingchallenge.ticketing.services.exceptions.TicketServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A Simple Concrete Implementation of TicketService
 */
public class SimpleTicketService implements TicketService {

    /**
     * Default seat hold expiration time is 60 seconds.
     */
    private static final long DEFAULT_SEAT_HOLD_TIMEOUT = 60;
    /**
     * An atomic integer for generation of ID's. Keeping it simple here as opposed to generating GUID's
     * and shrinking it or maintaining a collection for checking uniqueness.
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * A Map that holds all the SeatHold objects currently active.
     */
    private final Map<Integer, SeatHold> seatHoldMap = new HashMap<>();


    /**
     * The seathold expiration timeout.
     */
    private long seatHoldTimeout;
    /**
     * The venue object that holds seats, seat rating and layout
     */
    private Venue venue;


    /**
     * Default Constructor for SimpleTicketService
     *
     * @param venue
     */
    SimpleTicketService(final Venue venue) {
        this(venue, DEFAULT_SEAT_HOLD_TIMEOUT);
    }

    /**
     * Overloaded constructor providing seatHoldTimeout besides Venue
     *
     * @param venue
     * @param seatHoldTimeout
     */
    public SimpleTicketService(Venue venue, long seatHoldTimeout) {

        this.venue = venue;
        this.seatHoldTimeout = seatHoldTimeout;
    }

    //Start - Interface Implementation Block

    /**
     * The number of seats in the venue that are neither held nor reserved.
     *
     * @return the number of tickets available in the venue
     */
    @Override
    public synchronized int numSeatsAvailable() {
        return venue.getAvailableSeatCount();
    }


    /**
     * Find and hold the best available seats for a customer.
     *
     * @param numSeats      the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related information
     */
    @Override
    public synchronized SeatHold findAndHoldSeats(int numSeats, String customerEmail) {

        checkArgument(!customerEmail.isEmpty(), "Please provide a valid email address");
        checkArgument(numSeats > 0, "Please provide a valid number of seats you want to hold.");
        checkArgument(numSeats < venue.getTotalSeats(), "You are requesting to hold more than the whole venue. ");

        if (this.venue.getAvailableSeatCount() < numSeats) {

            throw new TicketServiceException(String.format("We don't have %d many seats available right now.", numSeats));
        }

        // Now that we have taken care of input validation, let's see how we want to find the best seats.
        // Since it's a rectangular venue, We'll consider the seat layout in terms of rows and columns.

        // Step 1: Find all requested seats in sequence.
        int[] seats = venue.findSequentSeats(numSeats, null);

        if (seats.length > 0) {

            // Excellent!! We found all requested seats together in one row. Now let's go hold them.
            return holdSeats(numSeats, seats, customerEmail);

        } else {

            // Since we didn't find all seats together, now lets see if we can find in groups
            List<Integer> blockSeats = new ArrayList<>();
            int seatsFound = 0;
            int seatBlock = numSeats - 1;

            //We'll decrease the requested number of seats by one and repeat the lookup process
            while (seatBlock > 0 && (seatsFound <= numSeats)) {
                int[] seatIds = venue.findSequentSeats(seatBlock, blockSeats);
                if (seatIds.length > 0) {
                    seatsFound += seatIds.length;
                    seatBlock = numSeats - seatsFound;
                    for (int x : seatIds) {
                        blockSeats.add(x);
                    }
                } else {
                    seatBlock--;
                }
            }

            //If we found all seats in different blocks, hold them otherwise send empty SeatHold
            if (blockSeats.size() == numSeats) {

                int[] tempSeatIds = blockSeats.stream().mapToInt(s -> s).toArray();
                return holdSeats(numSeats, tempSeatIds, customerEmail);
            }
        }

        return new SeatHold();
    }

    /**
     * Just an internal method for code reuse.
     *
     * @param numSeats
     * @param seats
     * @return
     */
    private SeatHold holdSeats(int numSeats, int[] seats, String customerEmail) {

        SeatHold seatHold = new SeatHold(atomicInteger.incrementAndGet(), numSeats, seats, customerEmail);
        seatHoldMap.put(seatHold.getId(), seatHold);

        venue.holdSeats(seats, this.seatHoldTimeout);
        return seatHold;
    }


    /**
     * Commit seats held for a specific customer.
     *
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the seat hold is assigned
     * @return a reservation confirmation code
     */
    @Override
    public synchronized String reserveSeats(int seatHoldId, String customerEmail) {

        checkArgument(!customerEmail.isEmpty(), "Please provide a valid email address");
        checkArgument(seatHoldId > 0, "Please provide a valid SeatHold id.");

        String reservationCode = "";

        SeatHold seatHold = seatHoldMap.get(seatHoldId);


        if (seatHold == null) {
            throw new TicketServiceException(String.format("SeatHold with id: %d is either invalid or expired or reserved already.",
                    seatHoldId));
        }

        if (!seatHold.getCustomerEmail().equals(customerEmail)) {
            throw new TicketServiceException(String.format("SeatHold with id: %d is not related to customer email %s",
                    seatHoldId, customerEmail));
        }

        int[] reservedSeatIds = seatHold.getSeats();
        if (reservedSeatIds.length > 0) {
            reservationCode = "R" + seatHold.getId().toString();
            seatHoldMap.remove(seatHoldId);
            venue.reserveSeats(reservedSeatIds);
        }

        return reservationCode;
    }

}
