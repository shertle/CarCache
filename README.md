# CarCache

One of the inconveniences of driving is the familiar dance of circling a block to find a parking spot.  We created CarCache to streamline this process.  CarCache is an Android app that generates parking data and displays this data to the user.  Specifically, CarCache to allows the user to view recently vacated parking spots in their local area.

CarCache utilizes the Bluetooth connection between the user’s mobile device and their car to generate parking data.  When a departing user connects via Bluetooth to their car, we notify arriving users that a nearby parking spot has been vacated.  This data is displayed to arriving users on a map.  Arriving users can refresh the map at anytime to view recently vacated spots.  This service is performed in the background so that departing users do not have to do any work to notify arriving users.

Arriving users searching for a parking spot can view a map with markers indicating recently vacated parking spots. The marker’s opacity changes in parallel to how old the marker is. Users can also find where they have parked their car if users have forgotten. Furthermore, the user can click a specific marker to find out how recently the spot was vacated.  The user can then click a button to directly launch Google Maps to find directions to the parking spot.
