package se.gritacademy.mobilt_java23_oliver_schuller_api_intergration_v4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil.load

class ObservationFragment : Fragment(R.layout.fragment_observation) {
    lateinit var imageView: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        imageView.load("https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Ara_chloropterus_-Butterfly_World%2C_Florida%2C_USA-8a.jpg/375px-Ara_chloropterus_-Butterfly_World%2C_Florida%2C_USA-8a.jpg")

    }

}