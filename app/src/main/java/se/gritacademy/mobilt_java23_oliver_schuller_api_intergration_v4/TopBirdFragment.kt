package se.gritacademy.mobilt_java23_oliver_schuller_api_intergration_v4

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class TopBirdFragment : Fragment(R.layout.fragment_top_bird) {

    private lateinit var nextPage: Button
    private lateinit var backPage: Button
    private var page = 0
    private lateinit var birdNameTextView: TextView
    private lateinit var birdCommonName: String
    private lateinit var responseArray: JSONArray
    private lateinit var imageView: ImageView
    private lateinit var imageUrl: String
    private lateinit var wikiUrl: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        birdNameTextView = view.findViewById(R.id.birdName)
        nextPage = view.findViewById(R.id.nextBtn)
        backPage = view.findViewById(R.id.backBtn)
        imageView = view.findViewById(R.id.imageView)

        val eBirdUrl = "https://api.ebird.org/v2/data/obs/SE/recent/notable?key=t8njjjct7d5k"
        var queue = Volley.newRequestQueue(context)

        // Send request to eBird
        var stringRequest = StringRequest(Request.Method.GET, eBirdUrl,
            { response ->
                birdCommonName = JSONArray(response).getJSONObject(0).getString("comName")
                birdNameTextView.text = birdCommonName
                responseArray = JSONArray(response)
                wikiReq()
            },
            { Log.i("oliver", "error") })

        queue.add(stringRequest)
        queue.cache.clear()

        nextPage.setOnClickListener {
            changePage("forward")
        }

        backPage.setOnClickListener {
            changePage("back")
        }
    }

    private fun changePage(direction: String) {
        if (direction == "forward") {
            if (page < responseArray.length()) {
                page++
                birdCommonName = responseArray.getJSONObject(page).getString("comName")
                birdNameTextView.text = birdCommonName
                wikiReq()
            }
        } else if (direction == "back") {
            if (page > 0) {
                page--
                birdCommonName = responseArray.getJSONObject(page).getString("comName")
                birdNameTextView.text = birdCommonName
                wikiReq()
            }
        }
    }

    private fun wikiReq() {
        wikiUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages|pageprops&format=json&piprop=thumbnail&titles=${birdCommonName.lowercase().replace(" ", "%20")}&pithumbsize=300"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, wikiUrl,
            { response ->
                Log.i("oliver", "onViewCreated: $response")
                var data = JSONObject(response).getJSONObject("query").getJSONObject("pages")
                var nextKey = data.keys().next()

                // Checks if picture exists for bird
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
}
