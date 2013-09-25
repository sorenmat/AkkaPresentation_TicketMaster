List of commands

Create a new venue:

    curl -i -H "Accept: application/text" -X POST -d "U2,100" http://localhost:4567/addVenue

List venues available:

    curl http://localhost:4567/listVenues

List available tickets:

    curl http://localhost:4567/ticketsAvailable

Buy a ticket:

    curl -i -H "Accept: application/text" -X POST -d "U2,100" http://localhost:4567/buyTicket
