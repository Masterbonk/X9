package dk.itu.moapd.x9.ADJU.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.databinding.FragmentCreateReportBinding
import dk.itu.moapd.x9.ADJU.model.TrafficReport
import dk.itu.moapd.x9.ADJU.showToast
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel

class CreateReportFragment : Fragment() {

    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by activityViewModels()
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
            }

            buttonSever.setOnClickListener {
                viewModel.setState(getString(R.string.button_sever))
            }

            buttonEmergency.setOnClickListener {
                viewModel.setState(getString(R.string.button_emergency))
            }

            buttonSend.setOnClickListener {
                print("Running send")
                if (reportTitle.text.toString() == "" || description.text.toString() == "" || reportTitle.text.length > 60) {
                    showToast("Output invalid")
                    Log.d(TAG, "Output invalid")
                } else {

                    showToast("Sending output now: \n Title: ${reportTitle.text}, Description: ${description.text}, State: ${viewModel.state.value}")

                    Log.d(TAG, "Sending output now")
                    Log.d(
                        TAG,
                        "Title: ${reportTitle.text}, Description: ${description.text}, State: ${viewModel.state.value ?: "Mild"}"
                    )
                    viewModel.insertReport(
                        title = reportTitle.text.toString(),
                        description = description.text.toString(),
                        state = viewModel.state.value ?: "Mild"
                    )
                }
            }

        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}