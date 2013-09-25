package com.scalaprog.messages;

/**
 * User: soren
 */
public class CreateVenue {
    private final int tickets;
    private final String name;

    public CreateVenue(String name, int tickets) {
        this.name = name;
        this.tickets = tickets;
    }

    public String getName() {
        return name;
    }

    public int getTickets() {
        return tickets;
    }
}
