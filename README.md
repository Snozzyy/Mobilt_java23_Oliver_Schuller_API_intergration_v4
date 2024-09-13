# Bird Observer

Bird Observer is an application for bird watchers and provides the user with real-time data about birds observed in their area or country using eBird API.

## Features
* Birds near you: Displays birds observed within a 25 km perimeter from your device current position for the past 14 days.

* Notable birds: Displays notable birds that have been observed in your country for the past 14 days.

## APIs and endpoints used
### eBird API
* https://api.ebird.org/v2/data/obs/geo/recent?lat={lat}&lng={lng}
  * By inserting latitude and longitude as key values the API will respond with a list of birds observed in the area.
* https://api.ebird.org/v2/data/obs/{area_code}/recent/notable 
  * By inserting the country code as a key value the API will respond with all notable birds observed in the country.
      
### Wikipedia API
* https://en.wikipedia.org/w/api.php?action=query&prop=pageimages|pageprops&format=json&piprop=thumbnail&titles=${birdCommonName}&pithumbsize=300 
    * Will search for Wikipedia articles based on the key value, in this case the bird's name. If an image is used in the article then the image URL will be included in the response which is used by the application to display an image of the listed bird.
