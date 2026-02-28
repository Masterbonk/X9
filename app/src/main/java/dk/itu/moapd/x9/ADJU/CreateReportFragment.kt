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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dk.itu.moapd.x9.ADJU.MainActivity.Companion.TAG
import dk.itu.moapd.x9.ADJU.databinding.FragmentCreateReportBinding
import kotlin.getValue


class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by activityViewModels()
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

                    showToast("Sending output now: \n Title: ${reportTitle.text}, Description: ${description.text}, State: $STATE")

                    Log.d(TAG, "Sending output now")
                    Log.d(
                        TAG,
                        "Title: ${reportTitle.text}, Description: ${description.text}, State: $STATE"
                    )
                    viewModel.addItem(ItemsModel(
                        title = reportTitle.text.toString(),
                        description = description.text.toString(),
                        state = STATE
                    ))
                }
            }

        }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }

}
