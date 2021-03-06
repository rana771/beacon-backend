<script>
    $(document).ready(function() {
        initializeMap();
    });
    var map;
    var myCenter = new google.maps.LatLng(23.8103, 90.4125);
    var marker;

    function initializeMap() {
        infowindow = new google.maps.InfoWindow();
        latlng = new google.maps.LatLng(23.8103, 90.4125);

        var mapOptions = {
            zoom: 11,
            center: latlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        }

        geo = new google.maps.Geocoder();
        map = new google.maps.Map(document.getElementById("googleMap"), mapOptions);
        bounds = new google.maps.LatLngBounds();

        resultsPanel = new google.maps.DirectionsRenderer({suppressMarkers:true});
        resultsPanel.setMap(map);
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
        });
        var infowindow = new google.maps.InfoWindow({
            content: $("#name").val()+'<br>Lat: ' + location.lat() + '<br>Long: ' + location.lng()

        });
        $("#latitude").val(location.lat());
        $("#longitude").val(location.lng());
        infowindow.open(map, marker);
    }
</script>


<div class="form-group">
    <label class="col-md-1 control-label label-required" for="latitude">Latitude:</label>

    <div class="col-md-2">
        <input type="text" class="k-textbox" id="latitude" name="latitude" maxlength="255"
               required validationMessage="Required" data-bind="value: beacon.latitude"
               tabindex="3" autofocus/>
    </div>

    <div class="col-md-2 pull-left">
        <span class="k-invalid-msg" data-for="latitude"></span>
    </div>
    <label class="col-md-2 control-label label-required" for="longitude">Longitude:</label>

    <div class="col-md-2">
        <input type="text" class="k-textbox" id="longitude" name="longitude" maxlength="255"
               required validationMessage="Required" data-bind="value: beacon.longitude"
               tabindex="4" autofocus/>
    </div>

    <div class="col-md-2 pull-left">
        <span class="k-invalid-msg" data-for="longitude"></span>
    </div>
</div>

<div class="form-group">
    <label class="col-md-1 control-label label-required" for="tag">Tags:</label>

    <div class="col-md-8">
        <input type="text" class="k-textbox" id="tag" name="tag" maxlength="255"
               placeholder="Please use comma seperator for multiple tags" required
               validationMessage="Required" data-bind="value: beacon.tag"
               tabindex="1" autofocus/>
    </div>

    <div class="col-md-2 pull-left">
        <span class="k-invalid-msg" data-for="tag"></span>
    </div>

</div>

<div id="googleMap" style="width:400px;height:280px;"></div>
