package se.gritacademy.mobilt_java23_oliver_schuller_api_intergration_v4

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    lateinit var fragBtn1: Button
    lateinit var fragBtn2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fragBtn1 = findViewById(R.id.fragBtn1)
        fragBtn2 = findViewById(R.id.fragBtn2)

        val topBirdsFragment = TopBirdFragment()
        val observationsFragment = ObservationFragment()

        fragBtn1.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, observationsFragment)
                addToBackStack(null)
                commit()
            }
        }

        fragBtn2.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, topBirdsFragment)
                addToBackStack(null)
                commit()
            }
        }
    }
}