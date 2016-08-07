<%--
  Created by IntelliJ IDEA.
  User: ripon.rana
  Date: 7/28/2016
  Time: 11:03 AM
--%>
<script>

    $( document ).ready(function() {
        loadAPI();
    });

    var map;
    var myCenter = new google.maps.LatLng(23.8103, 90.4125);
    var marker;


    function loadAPI()
    {
        var script = document.createElement("script");
        script.src = "http://www.google.com/jsapi?key=AIzaSyBAsIfb03AJspbnwIAgiGobtC3mBXiYJUo&amp;callback=loadMaps";

        // script.src ="http://maps.googleapis.com/maps/api/js?key=AIzaSyBAsIfb03AJspbnwIAgiGobtC3mBXiYJUo&amp;callback=loadMaps";
        script.type = "text/javascript";
        document.getElementsByTagName("head")[0].appendChild(script);

    }
    // google.maps.event.addDomListener(window, 'load', initialize);
    function loadMaps()
    {
        //AJAX API is loaded successfully. Now lets load the maps api
        google.load("maps", "2", {"callback" : mapLoaded});

    }


    function mapLoaded()
    {
        //here you can be sure that maps api has loaded
        //and you can now proceed to render the map on page
        if (GBrowserIsCompatible())
        {
            map = new GMap2(document.getElementById("googleMap"));
            map.setMapType(G_NORMAL_MAP);
            map.setCenter(myCenter, 11);

            google.maps.event.addListener(map, 'click', function(event) {
                placeMarkers(event.latLng);
            });
        }
    }

    function placeMarkers(location) {
        var marker = new google.maps.Marker({
            position: location,
            map: map
        });
    }

    // 2nd map s

    function initialize() {
        var mapProp = {
            center: myCenter,
            zoom: 11,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

        google.maps.event.addListener(map, 'click', function (event) {
            placeMarker(event.latLng);
        });
    }

    function placeMarker(location) {
        if(marker)
            marker.setMap(null);
        marker = new google.maps.Marker({
            position: location,
            map: map
//                draggable: true,
//                animation: google.maps.Animation.DROP
        });
        var infowindow = new google.maps.InfoWindow({
            content: 'Beacon: vBeacon Location</br>'+'Latitude: ' + location.lat() + '<br>Longitude: ' + location.lng()
        });
        infowindow.open(map, marker);
    }



</script>

<div id="googleMap" style="width:500px;height:280px;"></div>