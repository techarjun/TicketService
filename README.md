# TicketService
A small coding challenge for implementing basic ticketing system funtionality

Implements a simple ticket service in Java that facilitates the discovery, temporary hold and final reservation of seats within a high-demand performance venue. 

## What the service provides
- Finds the number of seats available within the venue
- Finds and hold the best available seats on behalf of a customer
- Reserves and commit a specific group of held seats for a customer

## Assumptions / Comments

The following are few assumptions around this implementation

- Venue is rectangular in shape, the system assumes seats are on a single floor with "rows by columns" layout.
- Seats numbers are predefined which start from front to back and left to right.
- The "best" seat is always the one closer to the center of the stage. In other words, the front center seats are best.
- Row designation is from front to back i.e.,rows [1 , 2, 3, 4..] & similarly columns from left to right columns [1, 2, 3..]
- If there are 5 rows and 5 columns (5 X 5) in the venue (total 25 seats), row 1 and column 3 (1, 3) is considered the best 
  seat and hence we pre-assign it a higher weight. (Technically the lower the rating, the higher its weight). The seat ratings
  increase (lower weight) as we move away from the center of stage in concentric fashion.

## Solution Approach

Picking the best seat on behalf of customer needs some design ideas.

- There are several ways we can design the auto picking feature.
- Approach 1: The system can pick seats in a linear fashion from left to right & front to back of the venue.
- Approach 2: We can create a rating algorithm and preassign a rating to each seat in the venue. The algorithm can give more 
              weight to center seats that are closer to the stage. 
- Approach 3: Though simple, one of the drawback with the above 2 approaches is for use case when multiple seats are required
              in a single booking. Acknowledging the importance of this, assigning consecutive seats to group bookings is 
              considered more important in this system.
 ## Design
  - Assigning consecutive seats to group bookings is considered higher priority in the system. It takes precedence over pure
    seat rating. Seat rating is still used as secondary sort.
  - The scope of booking consecutive seats is limited to single row at this point. i.e., if we can't find all consecutive 
    seats in a row, we split the group.
   - Since the design needs to consider both making people sit next to each other and making them sit at best seats (with
     higher weight), we have at-least 2 simple implementation approaches here..
     1. Break rows into say "groups" as seats are held and maintain these groups i.e., split and join as seats are 
        held/expired in a linkedlist like data structure. 
     2. Or Use more compute cycles to programmatically walk through seats on every seat hold request. 
   - Let's use approach 2 for this system.
   - Instead of using a seperate thread for invalidating held seats after certain time, a lazy approach of expiring the holds
     on seats is adapted in this implementation.
   
    
 ## Implementation
  - Implementation is done using Java 1.10 and gradle as build tool
  - A small Main.java file is included to enable running this as a console app on command prompt.
  - You can configure basic attributes in this main.java file. See below code snippet for an idea..
         
          private static final int VENUE_ID = 1;
          private static final int NUMBER_OF_ROWS = 10;
          private static final int SEATS_PER_ROW = 10;
          private static final int SEAT_HOLD_TIMEOUT = 60;

          public static void main(String[] args) {

              Venue venue = new Venue(VENUE_ID, NUMBER_OF_ROWS, SEATS_PER_ROW);
              TicketService ticketService = new SimpleTicketService(venue, SEAT_HOLD_TIMEOUT);
              
  ## How to Run 
   - Run with gradle clean build or gradle clean build test. 
   - There is a stress test included for 5000 seats.
   - Get the repository on to a folder on your machine
   - Build Command:   ./gradlew build (Mac)         or ./gradle build (windows)
   - Run Console App:  ./gradlew run  (Mac)         or ./gradle (windows)
   - Run Tests: ./gradlew clean test --info (Mac)   or ./gradle clean test --info (windows)
