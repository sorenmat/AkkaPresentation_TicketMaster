package com.scalaprog.messages;

/**
 * User: soren
 */
public class BuyTicket {
    private final String venue;

    public BuyTicket(String venue) {
        this.venue = venue;
    }

    public String getVenue() {
        return venue;
    }
}
