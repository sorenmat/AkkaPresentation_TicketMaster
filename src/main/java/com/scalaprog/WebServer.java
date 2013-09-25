package com.scalaprog;

import akka.actor.*;
import akka.util.*;
import com.scalaprog.actors.*;
import com.scalaprog.messages.*;
import scala.concurrent.*;
import scala.concurrent.duration.*;
import spark.*;

import java.util.*;

import static java.lang.Integer.valueOf;
import static spark.Spark.get;
import static spark.Spark.post;

/**
 * This is a simple web server for hosting the TicketNet.
 * It has some nasty blocking Await, due to the fact it's not an actor based web server.
 */
public class WebServer {

    public static void main(String[] args) {
        // Create the main ActorSystem
        final ActorSystem system = ActorSystem.create("TicketNet");

        // Create the one and only ticketMaster
        final ActorRef ticketMaster = system.actorOf(new Props(TicketMaster.class), "ticketMaster");

        /**
         * List all the venues.
         * Sends a message to the ticketmaster
         */
        get(new Route("/listVenues") {
            @Override
            public Object handle(Request request, Response response) {

                Timeout timeout = new Timeout(Duration.create(5, "seconds"));
                Future<Object> future = akka.pattern.Patterns.ask(ticketMaster, new ListVenues(), timeout);
                try {
                    String result = (String) scala.concurrent.Await.result(future, timeout.duration());
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "error !";
            }

        });

        /**
         * Get a list of all tickets available, for all venues
         * The ticketmaster will ask all of it's children to return it's name and current ticket count
         */
        get(new Route("/ticketsAvailable") {
            @Override
            public Object handle(Request request, Response response) {

                Timeout timeout = new Timeout(Duration.create(5, "seconds"));
                Future<Object> future = akka.pattern.Patterns.ask(ticketMaster, new TicketsAvailable(), timeout);
                try {
                    String result = (String) scala.concurrent.Await.result(future, timeout.duration());
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "error !";
            }

        });


        /**
         * Add a new venue.
         * Should be posted in the format of name,#tickets
         */
        post(new Route("/addVenue") {
            @Override
            public Object handle(Request request, Response response) {
                String body = request.body();
                StringTokenizer st = new StringTokenizer(body, ",");
                String venueName = st.nextToken();
                String tickets = st.nextToken();
                ticketMaster.tell(new CreateVenue(venueName, valueOf(tickets)), null);
                return "ok";
            }

        });

        /**
         * Buy a ticket from a venue
         * Should be posted in the format of name,#tickets
         */
        post(new Route("/buyTicket") {
            @Override
            public Object handle(Request request, Response response) {
                String venue = request.body();
                Timeout timeout = new Timeout(Duration.create(5, "seconds"));
                Future<Object> future = akka.pattern.Patterns.ask(ticketMaster, new BuyTicket(venue), timeout);
                try {
                    String result = (String) scala.concurrent.Await.result(future, timeout.duration());
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "error";
            }

        });

    }
}