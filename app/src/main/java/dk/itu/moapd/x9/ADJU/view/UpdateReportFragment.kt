package dk.itu.moapd.x9.ADJU.view

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.databinding.FragmentUpdateReportBinding
import dk.itu.moapd.x9.ADJU.showToast
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel

class UpdateReportFragment() : Fragment() {

    private var _binding: FragmentUpdateReportBinding? = null

    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by activityViewModels()
    companion object {
        val TAG = MainActivity::class.qualifiedName
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUpdateReportBinding.inflate(inflater, container, false)

        if (viewModel._selected_report_key.value == null){
            findNavController().navigate(R.id.action_update_to_main)
        }
        setupUI()


        Log.d(TAG, "onCreateView() method called.")


        return binding.root
    }

    private fun setupUI() =
        with(binding) {//button_mild id becomes buttonMild here

            reportTitle.text = SpannableStringBuilder(viewModel._selected_report_title.value?:"")
            description.text = SpannableStringBuilder(viewModel._selected_report_description.value?:"")

            buttonMild.setOnClickListener {
                viewModel.setState(getString(R.string.button_mild))
            }

            buttonSever.setOnClickListener {
                viewModel.setState(getString(R.string.button_sever))
            }

            buttonEmergency.setOnClickListener {
                viewModel.setState(getString(R.string.button_emergency))
            }

            buttonUpdate.setOnClickListener {
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
                    viewModel.updateReport(
                        title = reportTitle.text.toString(),
                        description = description.text.toString(),
                        state = viewModel.state.value ?: "Mild",
                        key = viewModel._selected_report_key.value ?: "null", //Should probably be something better
                        createdAt = null,
                        latitude = viewModel._selected_report_lat.value ?: 0.0,
                        longtitude = viewModel._selected_report_lng.value ?: 0.0,
                        imageName = viewModel._selected_report_filename.value ?: "",
                    )

                    viewModel._selected_report_key.value = null

                    findNavController().navigate(R.id.action_update_to_main)

                }
            }
        }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}