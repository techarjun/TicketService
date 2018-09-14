package com.walmartlabs.codingchallenge.ticketing.domain;


import java.util.Calendar;

import static com.google.common.base.Preconditions.checkState;

/**
 * A domain object representing the entity seat in a venue.
 */
class Seat {

    /**
     * Seat unique id
     */
    private int seatId;

    /**
     * Seat row index
     */
    private int row;

    /**
     * Seat column
     */
    private int column;

    /**
     * Seat rating
     */
    private float rating;

    /**
     * Indicates if this seat is reserved or not
     */
    private boolean isReserved;

    /**
     * Indicates if this seat is held or not
     */
    private boolean isHeld;

    /**
     * Expiration time of for this seat to transition from hold to being available
     */
    private Calendar expires;


    /**
     * Constructor with seatId, row, column and rating
     */
    Seat(int seatId, int row, int column, float rating) {
        super();
        this.seatId = seatId;
        this.row = row;
        this.column = column;
        this.rating = rating;
        this.isReserved = false;
        this.isHeld = false;
    }

    /**
     * Check if seat is available. Instead of running a seperate thread to turn held seats to available state, doing that
     * here as a lazy operation.
     */
    public boolean isAvailable() {

        if (this.isHeld) {
            Calendar now = Calendar.getInstance();
            if ((this.expires != null) && now.getTimeInMillis() >= this.expires.getTimeInMillis()) {
                this.isHeld = false;
            }
        }
       return !(this.isHeld || this.isReserved);
    }


    /**
     * Changes the state of this seat to being Held
     *
     */
    void setHold(boolean isHeld) {

        if(isHeld)
        {
            checkState(isAvailable(), "Cannot hold an unavailable seat");
        }
        this.isHeld = isHeld;
    }


    /**
     * Marks this seat to expire from hold state and return back to available state after this duration
     *
     * @param expires
     */
    void setExpires(Calendar expires) {
        this.expires = expires;
    }

    /**
     * Indicated if this seat is reserved or not
     *
     * @return
     */
    boolean isReserved() {
        return isReserved;
    }

    /**
     * Changes the state of this seat to reserved
     *
     * @param isReserved
     */
    void setReserved(boolean isReserved) {

        if (isReserved) {
            checkState(!this.isReserved, "Seat is already reserved");
        }
        this.isReserved = isReserved;
    }

    /**
     * Unique ID for this seat
     *
     * @return
     */
    public int getSeatId() {
        return seatId;
    }

    /**
     * Rating of this seat
     *
     * @return
     */
    public float getRating() {
        return rating;
    }

}