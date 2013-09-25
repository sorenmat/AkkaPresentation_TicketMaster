package com.scalaprog.messages;

import java.util.*;

/**
 * User: soren
 */
public class VenueList {
    private final List<String> venues;

    public VenueList(List<String> venues) {
        this.venues = venues;
    }

    public List<String> getVenues() {
        return venues;
    }

    public String toString() {
        String result = "";
        for(String venue: venues) {
            result += venue+" ";
        }
        return result;
    }
}
