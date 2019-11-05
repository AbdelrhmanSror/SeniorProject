package com.example.home.ui.authuntication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.home.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class AuthFragment : Fragment() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    companion object{
        const val SIGN_IN_CODE = 5

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpFirebaseUi()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        Log.v("placesApi", "resume ")

        firebaseAuth.addAuthStateListener(mAuthStateListener)

        super.onResume()
    }

    override fun onPause() {
        Log.v("placesApi", "pause ")
        firebaseAuth.removeAuthStateListener(mAuthStateListener)

        super.onPause()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //prepareMap()
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Log.v("placesApi", "donelogin ")

                findNavController().navigate(R.id.action_authFragment_to_navMapFragment)

               /* Snackbar.make(
                    binding.coordinatorLayout,
                    "successfully signed in, ${user?.displayName}",
                    Snackbar.LENGTH_LONG
                ).show()*/
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
               /* Snackbar.make(
                    binding.coordinatorLayout,
                    "failed to signed in",
                    Snackbar.LENGTH_LONG
                ).show()*/
                activity?.finish()


                //activity!!.finish()
            }
        }

    }

    private fun setUpFirebaseUi() {
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            when (it.currentUser) {
                null -> { //user is signed out
                    //onSignedOutCleanUp()
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()

                    )
                    // Create and launch sign-in intent
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            //automatically allow phone to save the user credential and try to log them in true to enable and false to disable
                            .setIsSmartLockEnabled(false)
                            .setTheme(R.style.Theme_MaterialComponents_Light)
                            .setAvailableProviders(providers)
                            .build(),
                        SIGN_IN_CODE
                    )
                }


                else -> {
                    // user is signed in}
                    Log.v("placesApi", "alreadylogin ")

                    findNavController().navigate(R.id.action_authFragment_to_navMapFragment)
                }
            }
        }
    }
}
