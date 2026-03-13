package dk.itu.moapd.x9.ADJU

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.ADJU.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //private val viewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var appBarConfiguration: AppBarConfiguration
    companion object {
        public val TAG = MainActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fm = supportFragmentManager


        val navController =
            (
                    fm.findFragmentById(R.id.fragment_container_view)
                            as NavHostFragment
                    ).navController



        appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.bottomNavigation.setupWithNavController(navController)

        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()

        Log.d(TAG, "onCreate() method called.")
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser ?: startLoginActivity()
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
}