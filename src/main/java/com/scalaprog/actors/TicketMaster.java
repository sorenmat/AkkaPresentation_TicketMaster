package com.scalaprog.actors;


import akka.actor.*;
import akka.util.*;
import com.scalaprog.messages.*;
import scala.concurrent.*;
import scala.concurrent.Await;
import scala.concurrent.duration.*;

import java.util.*;

/**
 * User: soren
 */
public class TicketMaster extends UntypedActor {
    List<String> venues = new ArrayList<String>();

    /**
     * To be implemented by concrete UntypedActor, this defines the behavior of the
     * UntypedActor.
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ListVenues) {
            handleListVenues();
        } else if (message instanceof CreateVenue) {
            createVenue((CreateVenue) message);
        } else if (message instanceof TicketsAvailable) {
            ticketsAvailable();
        } else if (message instanceof BuyTicket) {
            buyTicket((BuyTicket) message);
        }
    }

    /**
     * Buy a ticket for a specifc venue.
     * First find the venue, and then send the request
     * @param ticket
     */
    private void buyTicket(BuyTicket ticket) {
        context().actorFor(ticket.getVenue()).tell(ticket, getSender()); // set sender as reply to
    }

    /**
     * Create a string with all the venues and tickets available.
     * This uses the ask method, and waits for an answer to compose the result
     */
    private void ticketsAvailable() {
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        List<Future<Object>> result = new ArrayList<Future<Object>>();

        for (ActorRef venue : getContext().getChildren()) { // get All venueActors
            result.add(akka.pattern.Patterns.ask(venue, new TicketsAvailable(), timeout));
        }

        try {
            String str = "";
            for (Future<Object> t : result) {
                str += Await.result(t, timeout.duration()) + "\n";
            }
            getSender().tell(str, getSelf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new actor for this venue, with the name from the message
     * @param cv
     */
    private void createVenue(CreateVenue cv) {
        venues.add(cv.getName());
        ActorRef venueActor = getContext().actorOf(new Props(VenueActor.class), cv.getName());
        venueActor.forward(cv, getContext()); // forward message to actor
    }

    /**
     * Tell the sender the internal state of the venues list
     */
    private void handleListVenues() {
        System.out.println("Got ListVenues message");
        final VenueList msg = new VenueList(venues);
        context().sender().tell(msg.getVenues().toString(), null);
    }
}
