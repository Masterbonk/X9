package dk.itu.moapd.x9.ADJU.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import dk.itu.moapd.x9.ADJU.view.LoginActivity
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.core.DATABASE_URL
import dk.itu.moapd.x9.ADJU.databinding.ActivityMainBinding
import dk.itu.moapd.x9.ADJU.service.LocationService
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
//https://www.geeksforgeeks.org/android/how-to-build-a-weather-app-in-android/

    private lateinit var weatherText: TextView
    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var hasPermission: Boolean = false

    //private val viewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        val TAG = MainActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherText = findViewById(R.id.weather_text)


        val fm = supportFragmentManager

        val navController =
            (
                    fm.findFragmentById(R.id.fragment_container_view)
                            as NavHostFragment
                    ).navController

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.bottomNavigation.setupWithNavController(navController)

        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()

        with(binding) {
            logoutButton.setOnClickListener {
                auth.signOut()
                startLoginActivity()
            }
        }


        Log.d(TAG, "onCreate() method called.")
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser ?: startLoginActivity()
        checkLocationPermission()
        if (hasPermission){
            fetchLocation()
        }
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

    /**
     * This method starts the login activity which allows the user log in or sign up to the Firebase
     * Authentication application.
     *
     * Before accessing the main activity, the user must log in the application through a Firebase
     * Auth backend service. The method starts a new activity using explicit intent and used the
     * method `finish()` to disable back button.
     */
    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            // Alternative to calling finish(): clears the back stack.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }


    private fun checkLocationPermission() {

        hasPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted

            if (!granted) {
                // Handle denial (snackbar, toast, etc.)
            }
        }

        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=metric&appid=$API_KEY"
                    // Debugging: Log URL
                    println("Weather API URL: $weatherUrl")

                    fetchWeatherData(weatherUrl)
                } else {
                    weatherText.text = "Could not get location."
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchWeatherData(url: String) {
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val main = jsonResponse.getJSONObject("main")
                    val temperature = main.getString("temp")
                    val city = jsonResponse.getString("name")

                    // Update UI with fetched data
                    weatherText.text = "$temperature°C in $city"
                } catch (e: Exception) {
                    weatherText.text = "Error parsing data!"
                    e.printStackTrace()
                }
            },
            { error ->
                weatherText.text = "Error fetching weather!"
                error.printStackTrace()
            })

        queue.add(request)
    }

    @SuppressLint("MissingPermission") // safe because you already checked
    private fun getLocationOnce() {
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude

                    Log.d("LOCATION", "Lat: $lat, Lng: $lng")
                } else {
                    Log.d("LOCATION", "Location is null")
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}