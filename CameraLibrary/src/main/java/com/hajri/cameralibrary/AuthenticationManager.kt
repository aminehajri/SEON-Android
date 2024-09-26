package com.hajri.cameralibrary

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AuthenticationManager(private val context: Context) {

    fun authenticateUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(context)
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Authenticate")
                    .setDescription("Please use biometric authentication to access the photos.")
                    .setNegativeButtonText("Cancel")
                    .build()

                val biometricPrompt = BiometricPrompt(
                    context as FragmentActivity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            onSuccess()
                        }

                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            onError(errString.toString())
                        }

                        override fun onAuthenticationFailed() {
                            onError("Authentication failed. Please try again.")
                        }
                    })

                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                onError("No biometric hardware available.")

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                onError("Biometric hardware is unavailable.")

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                onError("No biometric credentials enrolled.")

            else -> onError("Authentication not supported on this device.")
        }
    }
}