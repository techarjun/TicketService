package com.walmartlabs.codingchallenge.ticketing.domain;


/**
 * SeatHold represents a temporary reservation object which holds reservation information
 * It has it's unique identifier and seats that have been held.
 */
public class SeatHold {

    private Integer id;
    private int numSeats;
    private int [] seats;
    /**
     * The customers email.
     */
    private String customerEmail;

    /**
     * Empty Constructor
     */
    public SeatHold() {
    }

    /**
     * Constructor
     * @param id the unique hold identifier
     * @param numSeats the number of seats that were held.
     */
    SeatHold(Integer id, int numSeats) {
        this.id = id;
        this.numSeats = numSeats;
    }

    /**
     * Constructor
     * @param id
     * @param numSeats
     * @param seats
     */
    public SeatHold(Integer id, int numSeats, int[] seats, String email)
    {
        this.id = id;
        this.numSeats = numSeats;
        this.seats=seats;
        this.customerEmail = email;
    }

    /**
     * Gets the customer Email
     * @return
     */
    public String getCustomerEmail() {
        return this.customerEmail;
    }

    /**
     * @return the seathold id
     */
    public Integer getId() {
        return id;
    }


    /**
     * @return the number of seats that are being held
     */
    public int getNumSeats() {
        return numSeats;
    }

    public int[] getSeats() {
        return seats;
    }


    /**
     * Methods to aid in collection management
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeatHold seatHold = (SeatHold) o;

        return id.equals(seatHold.id);
    }

    /**
     * Methods to aid in collection management
     * @return
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
