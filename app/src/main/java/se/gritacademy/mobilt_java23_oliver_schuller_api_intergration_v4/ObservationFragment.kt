package se.gritacademy.mobilt_java23_oliver_schuller_api_intergration_v4

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import coil.load
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONObject


class ObservationFragment : Fragment(R.layout.fragment_observation) {
    private lateinit var imageView: ImageView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var birdCommonName: String
    private lateinit var location: String
    private lateinit var birdName: TextView
    private lateinit var nextPage: Button
    private lateinit var backPage: Button
    private lateinit var locationText: TextView
    private lateinit var responseArray: JSONArray
    private lateinit var imageUrl: String
    private lateinit var wikiUrl: String
    private var page = 0

    private val CHANNEL_ID = "notification_channel"
    var longitude: String = ""
    var latitude: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createNotificationChannel()

        birdName = view.findViewById(R.id.birdName)
        nextPage = view.findViewById(R.id.nextBtn)
        backPage = view.findViewById(R.id.backBtn)
        locationText = view.findViewById(R.id.locationText)
        imageView = view.findViewById(R.id.imageView)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        imageView = view.findViewById(R.id.imageView)

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
            return
        }

        // Get the device current coordinate and run apiCall() once coordinates are fetched
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                sendNotification()
                // Make coordinate compatible with API
                latitude = String.format("%.2f", it.latitude).replace(",", "%2E")
                longitude = String.format("%.2f", it.longitude).replace(",", "%2E")

                eBirdCall()
            }
        }

        nextPage.setOnClickListener {
            changePage("forward")
        }

        backPage.setOnClickListener {
            changePage("back")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changePage(direction: String) {
        if (direction == "forward") {
            if (page < responseArray.length() - 1) {
                page++
            }
        } else if (direction == "back") {
            if (page > 0) {
                page--
            }
        }
        birdCommonName = responseArray.getJSONObject(page).getString("comName")
        location = responseArray.getJSONObject(page).getString("locName")
        birdName.text = birdCommonName
        locationText.text = "Observed: $location"
        wikiReq()
    }

    // Get data about observation
    @SuppressLint("SetTextI18n")
    private fun eBirdCall() {
        val eBirdUrl = "https://api.ebird.org/v2/data/obs/geo/recent?lat=$latitude&lng=$longitude&key=t8njjjct7d5k"
        var queue = Volley.newRequestQueue(context)

        var stringRequest = StringRequest(
            Request.Method.GET, eBirdUrl,
            { response ->
                birdCommonName = JSONArray(response).getJSONObject(0).getString("comName")
                location = JSONArray(response).getJSONObject(0).getString("locName")

                birdName.text = birdCommonName
                locationText.text = "Observed: $location"
                responseArray = JSONArray(response)
                wikiReq()

                Log.i("oliver", "onViewCreated: $response")
            },
            { Log.i("oliver", "error") })

        queue.add(stringRequest)
        queue.cache.clear()

    }

    // Call wikipedia API to get images
    private fun wikiReq() {
        // Make birdCommonName suitable for API parameter value
        birdCommonName = birdCommonName.lowercase().replace(" ", "%20")
        wikiUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages|pageprops&format=json&piprop=thumbnail&titles=${birdCommonName}&pithumbsize=300"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, wikiUrl,
            { response ->
                Log.i("oliver", "onViewCreated: $response")
                var data = JSONObject(response).getJSONObject("query").getJSONObject("pages")
                var nextKey = data.keys().next()

                // Checks if picture exists for bird, else display "not found" image
                if (data.getJSONObject(nextKey).has("thumbnail")) {
                    imageUrl = data.getJSONObject(nextKey).getJSONObject("thumbnail").getString("source")
                    imageView.load(imageUrl)
                } else {
                    imageView.load(R.drawable.baseline_image_not_supported_24)
                }

            },
            { Log.i("oliver", "error") })

        queue.add(stringRequest)
        queue.cache.clear()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Getting cordinates"
            val descriptionText = "Getting cordinates to show birds near you"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val intent = Intent(context, ObservationFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Bird Observer")
            .setContentText("Fetching cordinates from GPS")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            notify(1337, builder.build())
        }
    }
}