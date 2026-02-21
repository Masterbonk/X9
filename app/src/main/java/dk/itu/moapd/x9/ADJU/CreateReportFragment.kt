package dk.itu.moapd.x9.ADJU

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dk.itu.moapd.x9.ADJU.MainActivity.Companion.TAG
import dk.itu.moapd.x9.ADJU.databinding.ReportActivityBinding
import kotlin.getValue


class CreateReportFragment : Fragment() {

    private lateinit var binding: ReportActivityBinding

    private val viewModel: ReportViewModel by viewModels()

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val STATE = "Mild"
        private const val TITLE = "Title"
        private const val DESCRIPTION = "Description"
        private const val TYPE = "Broken traffic light"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_report, container, false)
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
                //val myIntent: Intent = Intent(this@CreateReportFragment, MainActivity::class.java)
                //myIntent.putExtra("Title", reportTitle.text.toString()) //Optional parameters
                //myIntent.putExtra("Type", reportType.selectedItem.toString()) //Optional parameters
                //myIntent.putExtra("Description", description.text.toString()) //Optional parameters
                //myIntent.putExtra("State", STATE) //Optional parameters
                //this@ReportActivity.startActivity(myIntent)
            }
        }


}