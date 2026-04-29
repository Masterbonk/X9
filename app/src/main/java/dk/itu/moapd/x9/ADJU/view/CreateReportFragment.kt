package dk.itu.moapd.x9.ADJU.view


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices
import dk.itu.moapd.x9.ADJU.R
import dk.itu.moapd.x9.ADJU.databinding.FragmentCreateReportBinding
import dk.itu.moapd.x9.ADJU.showToast
import dk.itu.moapd.x9.ADJU.viewmodel.ReportViewModel


open class CreateReportFragment : Fragment() {

    private var _binding: FragmentCreateReportBinding? = null
    open val binding get() = _binding!!

    private var pendingLocationRequest = false

    //Get permission for this location
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingLocationRequest) {
            getCurrentLocation()
        }
    }

    private var capturedImageUri: Uri? = null

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCameraIntent(
                context = requireContext(),
                onUriCreated = { uri -> capturedImageUri = uri },
                onLaunch = { intent -> cameraLauncher.launch(intent) },
            )
        } else {
            showToast("Camera permission required")
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Image successfully captured
            Log.d("Camera", "Image URI: $capturedImageUri")

        } else {
            Log.d("Camera", "Camera cancelled")
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

            buttonTakePic.setOnClickListener {
                //Get permission & take picture
                checkCameraPermissionAndOpen()
            }

            buttonSend.setOnClickListener {
                var hasPermission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED


                if (reportTitle.text.toString() == "" || description.text.toString() == "" || reportTitle.text.length > 60) {
                    showToast("Output invalid")
                } else if(!hasPermission){
                    showToast("Getting permission")
                    pendingLocationRequest = true
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }else {
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
            Manifest.permission.ACCESS_FINE_LOCATION
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
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            getCurrentLocation()
        } else {
            pendingLocationRequest = true
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
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
        longtitude = lng,
        image = capturedImageUri ?: Uri.EMPTY,
        )
    }

    /**
     * Launches the camera intent to capture a photo.
     *
     * @param context The context of the application.
     * @param onUriCreated The callback to be invoked when the URI is created.
     * @param onLaunch The callback to be invoked when the camera intent is launched.
     */
    private fun launchCameraIntent(
        context: Context,
        onUriCreated: (Uri) -> Unit,
        onLaunch: (Intent) -> Unit,
    ) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(context.packageManager) == null) return

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "ML Kit")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: return

        onUriCreated(uri)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        onLaunch(cameraIntent)
    }

    private fun checkCameraPermissionAndOpen() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            launchCameraIntent(
                context = requireContext(),
                onUriCreated = { uri -> capturedImageUri = uri },
                onLaunch = { intent -> cameraLauncher.launch(intent) },
            )
        } else {
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
}

