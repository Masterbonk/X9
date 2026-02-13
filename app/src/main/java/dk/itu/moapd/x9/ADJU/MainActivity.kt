package dk.itu.moapd.x9.ADJU

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.x9.ADJU.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()


    companion object {
        public val TAG = MainActivity::class.qualifiedName
        private const val STATE = "Mild"
        private const val TITLE = "Title"
        private const val DESCRIPTION = "Description"
        private const val TYPE = "Broken traffic light"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        intent = getIntent();
        val i_title = intent.getStringExtra("Title") ?: "No Title received"
        val i_type = intent.getStringExtra("Type") ?: "No type received"
        val i_description = intent.getStringExtra("Description") ?: "No description received"
        val i_state = intent.getStringExtra("State") ?: "No state received"

        Log.d(
            MainActivity.Companion.TAG,
            "Title: ${i_title}, Type: ${i_type}, Description: ${i_description}, State: ${i_state}"
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_title)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         */


        //restoreState(savedInstanceState)
        setupUI()

        Log.d(TAG, "onCreate() method called.")
    }

    private fun setupUI() =
        with(binding) {//button_mild id becomes buttonMild here
            buttonGotoReport.setOnClickListener {
                val myIntent: Intent = Intent(this@MainActivity, ReportActivity::class.java)
                //myIntent.putExtra("key", value) //Optional parameters
                this@MainActivity.startActivity(myIntent)
            }

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