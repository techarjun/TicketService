package com.walmartlabs.codingchallenge.ticketing.domain;

import java.util.*;

/**
 * A venue similar to a theatre. The layout of this venue is more like a theatre.
 */
public class Venue {


    private final int id;
    private final int rows;
    private final int seatsPerRow;

    private final Map<Integer, Seat> seats;

    public Venue(int id, int rows, int seatsPerRow) {
        this.id = id;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        seats = new HashMap<>();


        int counter = 1;
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                float seatRating = getSeatRating(j, i, seatsPerRow);
                seats.put(counter, new Seat(counter, i, j, seatRating));
                counter++;
            }
        }

    }

    private float getSeatRating(int seatIndex, int rowIndex, int seatsPerRow) {

        float seatScore = Math.abs(((float) (seatsPerRow - 1) / 2) - (seatIndex - 1));
        float rowScore = (float) rowIndex;
        return ((seatScore + rowScore));

    }

    public synchronized int getAvailableSeatCount() {

        return (int) seats.values().stream().filter(s -> s.isAvailable()).count();
    }


    /**
     * @return the total number of seats in the venue
     */
    public int getTotalSeats() {
        return rows * seatsPerRow;
    }

    /**
     * Picks sequential seats that belong to same row i.e., sequence doesn't flow across rows
     *
     * @param excludeSeats
     * @return
     */
    public int[] findSequentSeats(int numSeats, List<Integer> excludeSeats) {
        List<Integer> seatIds;

        for (int i = 1; i <= rows; i++) {
            seatIds = findSequentSeats(numSeats, i, excludeSeats);
            if (seatIds.size() == numSeats) {
                return seatIds.stream().mapToInt(s -> s).toArray();
            }
        }

        return new int[0];
    }


    /**
     * Picks sequential seats from the given row i.e., sequence doesn't flow across rows
     *
     * @param excludeSeats
     * @return
     */
    private List<Integer> findSequentSeats(int numSeats, int row, List<Integer> excludeSeats) {

        float minAverage = Float.MIN_VALUE;
        List<Integer> sequentSeatIds = new ArrayList<>();
        List<Integer> seatIds = new ArrayList<>();

        row = (row - 1) * seatsPerRow;

        for (int i = 1; i <= (seatsPerRow - numSeats + 1); i++) {
            float sum = 0.0f;
            Boolean skip = false;
            seatIds.clear();

            for (int j = i; j < (i + numSeats); j++) {
                if (excludeSeats != null && excludeSeats.contains((row + j))) {
                    continue;
                }
                Seat seat = seats.get(row + j);
                if (seat.isAvailable()) {
                    sum += seat.getRating();
                    seatIds.add(seat.getSeatId());
                } else {
                    skip = true;
                    break;
                }
            }

            if (!skip) {
                float average = sum / numSeats;
                if ((minAverage == Float.MIN_VALUE) || (average < minAverage)) {
                    minAverage = average;
                    sequentSeatIds = new ArrayList<>(seatIds);
                }
            }
        }
        return sequentSeatIds;
    }

    /**
     * Changes the state of the given seats to hold for a given time.
     *
     * @param ids
     * @param holdTimeout
     */
    public void holdSeats(int[] ids, long holdTimeout) {

        for (int i = 0; i < ids.length; i++) {
            Seat s = seats.get(ids[i]);
            s.setHold(true);
            Calendar expires = Calendar.getInstance();
            expires.add(Calendar.SECOND, (int) holdTimeout);
            s.setExpires(expires);
        }
    }

    /**
     * Changes the state of the given seats to reserved.
     *
     * @param ids
     */
    public  void reserveSeats(int[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Seat s = seats.get(ids[i]);
            s.setExpires(null);
            s.setReserved(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Venue venue = (Venue) o;

        return id == venue.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    //For debugging purpose during development...may leave it in as-in
    public String printSeats() {

        StringBuilder sb = new StringBuilder();
        sb.append("\nSeat Layout | Format: A|H|R (Seat|Rating)");
        sb.append("\n-----------------------------------------------------\n");
        int counter = 1;
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                Seat seat = seats.get(counter++);

                if (seat.isAvailable()) {
                    sb.append(String.format("A(%d|%.01f)  ", seat.getSeatId(), seat.getRating()));
                } else if (seat.isReserved()) {
                    sb.append(String.format("R(%d|%.01f)  ", seat.getSeatId(), seat.getRating()));
                } else {
                    sb.append(String.format("H(%d|%.01f)  ", seat.getSeatId(), seat.getRating()));
                }
            }
            sb.append("\n");

        }
        sb.append("----------------------------------------------------\n");
        return sb.toString();
    }

}
