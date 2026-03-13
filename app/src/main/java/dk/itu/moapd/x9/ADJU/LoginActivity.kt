package dk.itu.moapd.x9.ADJU

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class LoginActivity : AppCompatActivity() {
    //Existing account: Email: Adj@itu.dk, password 123456789

    /**
     * This object launches a new activity and receives back some result data.
     */
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    /**
     * This method uses FirebaseUI to create a login activity with three sign-in/sign-up options,
     * namely: (1) by e-mail, (2) by phone number, and (3) by Google account. The user interface is
     * pre-defined and it uses the same theme (i.e., Material Design) to define the login activity
     * style.
     */
    private fun createSignInIntent() {

        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build())

        // Create and launch sign-in intent.
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.baseline_firebase_24)
            .setTheme(R.style.Theme_FirebaseAuthentication)
            .apply {
                setTosAndPrivacyPolicyUrls(
                    "https://firebase.google.com/terms/",
                    "https://firebase.google.com/policies/analytics"
                )
            }
            .build()
        signInLauncher.launch(signInIntent)
    }


    /**
     * When the second activity finishes (i.e., the pre-define login activity), it returns a result
     * to this activity. If the user sign-in the application correctly, we redirect the user to the
     * main activity of this application.
     *
     * @param result A result describing that the caller can launch authentication flow with a
     *      `Intent` and is guaranteed to receive a `FirebaseAuthUIAuthenticationResult` as
     *      result.
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        when (result.resultCode) {
            RESULT_OK -> {
                // Successfully signed in.
                //showSnackBar("User logged in the app.")
                startMainActivity()
            }
            else -> {
                // Sign in failed.
                //showSnackBar("Authentication failed.")
            }
        }
    }

    /**
     * In the case of successfully login, it opens the main activity and starts the Firebase
     * Authentication application.
     */
    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    /**
     * Displays a SnackBar to show a brief message about the clicked button.
     *
     * The SnackBar is created using the clicked button's information and is shown at the bottom of
     * the screen.
     *
     * @param message The message to be displayed in the SnackBar.
     */
    /*private fun showSnackBar(message: String) {
        window.decorView.rootView.showSnackBar(message)
    }

     */
}