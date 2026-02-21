package dk.itu.moapd.x9.ADJU

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.x9.ADJU.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    companion object {
        public val TAG = MainActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        /*intent = getIntent();
        val i_title = intent.getStringExtra("Title") ?: "No Title received"
        val i_type = intent.getStringExtra("Type") ?: "No type received"
        val i_description = intent.getStringExtra("Description") ?: "No description received"
        val i_state = intent.getStringExtra("State") ?: "No state received"



        Log.d(
            MainActivity.Companion.TAG,
            "Title: ${i_title}, Type: ${i_type}, Description: ${i_description}, State: ${i_state}"
        )

         */

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = supportFragmentManager

        /*
        val navController =
            (
                    supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                            as NavHostFragment
                    ).navController


         */
        //appBarConfiguration = AppBarConfiguration(navController.graph)

        //setupNavigation(navController)

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_title)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         */
        //restoreState(savedInstanceState)
        //setupUI()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set the default fragment that should be shown when the app starts
        setCurrentFragment(MainFragment())

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            // Check which menu item was clicked
            when (menuItem.itemId) {
                // If the Algorithm tab is selected, show the AlgorithmFragment
                R.id.fragment_main -> setCurrentFragment(MainFragment())
                // If the Course tab is selected, show the CourseFragment
                R.id.fragment_create_report -> setCurrentFragment(CreateReportFragment())
            }
            // Return true to indicate that we handled the item click
            true
        }

        Log.d(TAG, "onCreate() method called.")
    }

    private fun setupNavigation(navController: androidx.navigation.NavController) {
        // Portrait: bottom navigation.
        binding.bottomNavigation?.setupWithNavController(navController)
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            // Replace the fragment inside the container with the new fragment
            replace(R.id.fragment_container, fragment)
            // Commit the transaction to actually perform the change
            commit()
        }



/*
    private fun setupUI() =
        with(binding) {//button_mild id becomes buttonMild here
            buttonGotoReport.setOnClickListener {
                val myIntent: Intent = Intent(this@MainActivity, ReportActivity::class.java)
                //myIntent.putExtra("key", value) //Optional parameters
                this@MainActivity.startActivity(myIntent)
            }

        }

 */

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() method called.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() method called.")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() method called.")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() method called.")
    }
}