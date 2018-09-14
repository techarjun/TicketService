package com.walmartlabs.codingchallenge.ticketing.services.exceptions;

    public class TicketServiceException extends RuntimeException {

        /**
         * Constructs a TicketServiceException with the specified detail message.
         * @param message - the detail message.
         */
        public TicketServiceException(String message) {
            super(message);
        }
    }
