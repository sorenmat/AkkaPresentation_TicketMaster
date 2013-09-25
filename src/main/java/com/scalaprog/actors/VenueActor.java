package com.scalaprog.actors;

import akka.actor.*;
import akka.japi.*;
import com.scalaprog.messages.*;

/**
 * User: soren
 */
public class VenueActor extends UntypedActor {
    private int tickets;
    private String name;

    /**
     * Procedure for handling tickets while we still have them
     */
    Procedure<Object> stillHaveTickets = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message instanceof BuyTicket) {
                tickets = tickets - 1;
                if (tickets == 0)
                    getContext().become(outOfTickets);
                sender().tell("you got a ticket for "+name, getSelf());
            } else if (message instanceof TicketsAvailable) {
                sender().tell("Got " + tickets + " for " + name, getSelf());
            } else
                unhandled(message);
        }
    };

    /**
     * Procedure for handling tickets if we have no tickets left
     */
    Procedure<Object> outOfTickets = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message instanceof BuyTicket) {
                sender().tell(new SoldOut(), getSelf());
            } else if (message instanceof TicketsAvailable) {
                sender().tell("Got " + tickets + " for " + name, getSelf());
            } else unhandled(message);
        }
    };

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof CreateVenue) {
            CreateVenue cv = (CreateVenue) message;
            this.name = cv.getName();
            this.tickets = cv.getTickets();
            getContext().become(stillHaveTickets);
        } else if (message instanceof TicketsAvailable) {
            sender().tell("Got " + tickets + " for " + name, getSelf());

        } else unhandled(message);
    }
}
