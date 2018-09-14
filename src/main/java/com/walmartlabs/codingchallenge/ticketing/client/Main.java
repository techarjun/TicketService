package com.walmartlabs.codingchallenge.ticketing.client;

import com.walmartlabs.codingchallenge.ticketing.domain.SeatHold;
import com.walmartlabs.codingchallenge.ticketing.domain.Venue;
import com.walmartlabs.codingchallenge.ticketing.services.SimpleTicketService;
import com.walmartlabs.codingchallenge.ticketing.services.TicketService;

import java.util.Scanner;


public class Main {

    private static final int VENUE_ID = 1;
    private static final int NUMBER_OF_ROWS = 10;
    private static final int SEATS_PER_ROW = 10;
    private static final int SEAT_HOLD_TIMEOUT = 60;

    public static void main(String[] args) {

        Venue venue = new Venue(VENUE_ID, NUMBER_OF_ROWS, SEATS_PER_ROW);
        TicketService ticketService = new SimpleTicketService(venue, SEAT_HOLD_TIMEOUT);

        Scanner scanner = new Scanner(System.in);
        String email;
        boolean exit = false;


        while (!exit) {

            System.out.println("\n1.Seats Availability  2.Hold Seats  3.Reserve Seats  4.Print Seats  5.Exit");

            switch (ReadIntegerInput("Choose an option: ", scanner)) {

                case 1:

                    try {
                        System.out.println("Number of seats available : " + ticketService.numSeatsAvailable());

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

                    break;

                case 2:

                    System.out.print("Enter your email : ");
                    email = scanner.nextLine();

                    int numSeatsToHold = ReadIntegerInput("Enter number of seats to hold: ", scanner);

                    try {

                        SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToHold, email);
                        System.out.println("Your SeatHold ID : " + seatHold.getId());
                        System.out.println(venue.printSeats());

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

                    break;

                case 3:

                    System.out.println("Enter your email : ");
                    email = scanner.nextLine();

                    try {

                        int id = ReadIntegerInput("Enter SeatHold ID: ", scanner);
                        String confirmationCode = ticketService.reserveSeats(id, email);

                        System.out.println("Reservation Code : " + confirmationCode);
                        System.out.println(venue.printSeats());

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;

                case 4:

                    System.out.println(" ");
                    System.out.println(venue.printSeats());
                    System.out.println(" ");
                    break;

                case 0:

                   //Invalid input, let's try again.
                    break;

                default:
                    exit = true;
                    break;
            }
        }
    }


    private static int ReadIntegerInput(String prompt, Scanner scanner) {

        System.out.println(prompt);
        int i = 0;
        String s = scanner.nextLine();
        try {
            i = Integer.parseInt(s);
        } catch (Exception ex) {
            System.out.print("Invalid input.");
        }
        return i;
    }

}
