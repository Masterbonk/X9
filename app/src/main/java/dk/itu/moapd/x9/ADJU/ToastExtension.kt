package dk.itu.moapd.x9.ADJU

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(
    message: CharSequence,
) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}