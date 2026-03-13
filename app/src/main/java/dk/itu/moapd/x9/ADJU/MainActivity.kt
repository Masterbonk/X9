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

    //private val viewModel: MainViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    companion object {
        public val TAG = MainActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

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

        Log.d(TAG, "onCreate() method called.")
    }

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