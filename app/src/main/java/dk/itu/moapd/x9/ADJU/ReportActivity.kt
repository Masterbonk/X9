package dk.itu.moapd.x9.ADJU

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dk.itu.moapd.x9.ADJU.ReportViewModel
import dk.itu.moapd.x9.ADJU.databinding.ReportActivityBinding

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ReportActivityBinding

    private val viewModel: ReportViewModel by viewModels()



    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val STATE = "Mild"
        private const val TITLE = "Title"
        private const val DESCRIPTION = "Description"
        private const val TYPE = "Broken traffic light"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent = getIntent();

        enableEdgeToEdge()

        binding = ReportActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_title)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Found this from https://developer.android.com/develop/ui/views/components/spinner
        val spinner: Spinner = findViewById(R.id.report_type)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.report_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        restoreState(savedInstanceState)
        setupUI()

        Log.d(TAG, "onCreate() method called.")
    }

    private fun setupUI() =
        with(binding) {//button_mild id becomes buttonMild here
            buttonMild.setOnClickListener {
                viewModel.setState(getString(R.string.button_mild))
                //textViewMessage.text = getString(R.string.true_text)
            }

            buttonSever.setOnClickListener {
                viewModel.setState(getString(R.string.button_sever))
                //textViewMessage.text = getString(R.string.true_text)
            }

            buttonEmergency.setOnClickListener {
                viewModel.setState(getString(R.string.button_emergency))
                //textViewMessage.text = getString(R.string.true_text)
            }

            buttonSend.setOnClickListener {
                if(reportTitle.text.toString() == "" || description.text.toString() == "" || reportTitle.text.length > 60) {
                    Log.d(TAG, "Output invalid")
                } else {
                    viewModel.setTitle(reportTitle.text.toString())
                    viewModel.setType(reportType.selectedItem.toString())
                    viewModel.setDescription(description.text.toString())
                    Log.d(TAG, "Sending output now")
                    Log.d(
                        TAG,
                        "Title: ${reportTitle.text}, Type: ${reportType.selectedItem}, Description: ${description.text}, State: $STATE"
                    )
                }
            }

            buttonGotoMain.setOnClickListener {
                val myIntent: Intent = Intent(this@ReportActivity, MainActivity::class.java)
                myIntent.putExtra("Title", reportTitle.text.toString()) //Optional parameters
                myIntent.putExtra("Type", reportType.selectedItem.toString()) //Optional parameters
                myIntent.putExtra("Description", description.text.toString()) //Optional parameters
                myIntent.putExtra("State", STATE) //Optional parameters
                this@ReportActivity.startActivity(myIntent)
            }
        }


    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        binding.reportTitle.setText(savedInstanceState.getString(
            TITLE,
            getString(R.string.report_title_placeholder),
        ))
        binding.description.setText(savedInstanceState.getString(
            DESCRIPTION,
            getString(R.string.description_placeholder),
        ))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.apply {
            outState.putString(STATE, viewModel.state.toString())
        }
        binding.apply {
            outState.putString(TITLE, viewModel.title.toString())
        }
        binding.apply {
            outState.putString(DESCRIPTION, viewModel.description.toString())
        }
        binding.apply {
            outState.putString(TYPE, viewModel.type.toString())
        }

        Log.d(TAG, "onSaveInstanceState() method called.")
    }

    override fun onStart() {
        super.onStart()
        Log.d(MainActivity.Companion.TAG, "onStart() method called.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(MainActivity.Companion.TAG, "onResume() method called.")
    }

    override fun onPause() {
        super.onPause()
        Log.d(MainActivity.Companion.TAG, "onPause() method called.")
    }

    override fun onStop() {
        super.onStop()
        Log.d(MainActivity.Companion.TAG, "onStop() method called.")
    }
}