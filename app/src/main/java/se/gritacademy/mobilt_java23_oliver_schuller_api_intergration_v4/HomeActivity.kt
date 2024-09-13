package se.gritacademy.mobilt_java23_oliver_schuller_api_intergration_v4

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var nearMeBtn: Button
    private lateinit var notableBtn: Button
    private lateinit var logoutBtn: Button

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        nearMeBtn = findViewById(R.id.nearMeBtn)
        notableBtn = findViewById(R.id.notableBtn)
        logoutBtn = findViewById(R.id.logoutBtn)

        nearMeBtn.setOnClickListener {
            if (navController.currentDestination!!.id != R.id.observationFragment)
                navController.navigate(R.id.action_topBirdFragment_to_observationFragment)

        }

        notableBtn.setOnClickListener {
            if (navController.currentDestination!!.id != R.id.topBirdFragment)
                navController.navigate(R.id.action_observationFragment_to_topBirdFragment)
        }

        // Clears backstack and navigates to login screen
        logoutBtn.setOnClickListener {
            finishAffinity()
            navController.navigate(R.id.mainActivity)
        }
    }
}