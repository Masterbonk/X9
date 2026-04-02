package dk.itu.moapd.x9.ADJU.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.databinding.FragmentCreateReportBinding
import dk.itu.moapd.x9.ADJU.model.TrafficReport
import dk.itu.moapd.x9.ADJU.showToast
import dk.itu.moapd.x9.ADJU.view.MainActivity.Companion.TAG
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel

open class CreateReportFragment : Fragment() {

    private var _binding: FragmentCreateReportBinding? = null
    open val binding get() = _binding!!

    private var pendingLocationRequest = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingLocationRequest) {
            getCurrentLocation()
        }
    }
    private val viewModel: ReportViewModel by activityViewModels()

    companion object {
        val TAG = MainActivity::class.qualifiedName
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

    open fun setupUI() =
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
                if (reportTitle.text.toString() == "" || description.text.toString() == "" || reportTitle.text.length > 60) {
                    showToast("Output invalid")
                } else {
                    requestOrGetLocation()
                }
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getCurrentLocation() {
        val context = requireContext()

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            // Safety fallback (should not happen if flow is correct)
            Log.e(TAG, "Permission missing when trying to access location")
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude

                showToast("Sending output now")

                sendReportWithLocation(lat, lng)
            } else {
                Log.d(TAG, "Failed to send report")
            }
        }
    }

    private fun requestOrGetLocation() {
        val context = requireContext()

        var hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            getCurrentLocation()
        } else {
            pendingLocationRequest = true
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission){
            showToast("Need location to upload report")
        }
    }

    private fun sendReportWithLocation(lat: Double, lng: Double) {
        viewModel.insertReport(
        title = binding.reportTitle.text.toString(),
        description = binding.description.text.toString(),
        state = viewModel.state.value ?: "Mild",
        latitude = lat,
        longtitude = lng
        )
    }
}

