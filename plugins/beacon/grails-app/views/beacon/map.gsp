<%--
  Created by IntelliJ IDEA.
  User: ripon.rana
  Date: 7/28/2016
  Time: 11:03 AM
--%>

    <script
            src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBAsIfb03AJspbnwIAgiGobtC3mBXiYJUo">
    </script>

    <script>
        var map;
        var myCenter = new google.maps.LatLng(23.8103, 90.4125);
        var marker;

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

        google.maps.event.addDomListener(window, 'load', initialize);
    </script>



<div id="googleMap" style="width:500px;height:380px;"></div>



