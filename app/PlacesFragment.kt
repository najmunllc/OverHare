// PlacesFragment.kt
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class PlacesFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapPlaces) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize Places API
        placesClient = Places.createClient(requireContext())

        // Example: Search for nearby restaurants and add markers
        searchNearbyRestaurants()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun searchNearbyRestaurants() {
        val location = LatLng(37.7749, -122.4194) // San Francisco
        val radius = 5000 // 5 kilometers

        val request = FindCurrentPlaceRequest.builder()
            .locationBias(RectangularBounds.newInstance(location, radius.toDouble()))
            .build()

        placesClient.findCurrentPlace(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val likelyPlaces = task.result

                for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                    val place = placeLikelihood.place
                    val markerOptions = MarkerOptions()
                        .position(place.latLng!!)
                        .title(place.name.toString())
                        .snippet(place.address.toString())

                    googleMap.addMarker(markerOptions)
                }
            } else {
                val status: Status = task.exception?.status!!
                // Handle error
            }
        }
    }
}