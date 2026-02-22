package dk.itu.moapd.x9.ADJU

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(
    message: CharSequence,
    duration: Int = 30,
) {
    Toast.makeText(requireContext(), message, duration).show()
}