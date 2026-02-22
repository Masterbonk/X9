package dk.itu.moapd.x9.ADJU

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import dk.itu.moapd.x9.ADJU.MainActivity.Companion.TAG
import dk.itu.moapd.x9.ADJU.databinding.FragmentCreateReportBinding
import kotlin.getValue


class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by viewModels()

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val STATE = "Mild"
        private const val TITLE = "Title"
        private const val DESCRIPTION = "Description"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_title)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         */

        /*
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
         */


        _binding = FragmentCreateReportBinding.inflate(inflater, container, false)
        setupUI()

        Log.d(TAG, "onCreateView() method called.")

        return binding.root
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
                print("Running send")
                if (reportTitle.text.toString() == "" || description.text.toString() == "" || reportTitle.text.length > 60) {
                    showToast("Output invalid")
                    Log.d(TAG, "Output invalid")
                } else {

                    viewModel.setTitle(reportTitle.text.toString())
                    viewModel.setDescription(description.text.toString())

                    buttonSend.setOnClickListener {
                        showToast("Sending output now: \n Title: ${reportTitle.text}, Description: ${description.text}, State: $STATE")
                    }

                    Log.d(TAG, "Sending output now")
                    Log.d(
                        TAG,
                        "Title: ${reportTitle.text}, Description: ${description.text}, State: $STATE"
                    )
                }
            }

        }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }

}
